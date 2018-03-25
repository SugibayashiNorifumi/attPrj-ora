package application.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.servlet.LineBotCallbackException;
import com.linecorp.bot.servlet.LineBotCallbackRequestParser;

import application.controller.APIController;
import application.emuns.MenuCd;
import application.line.api.LineAPI;
import application.line.api.response.AccessToken;
import application.line.api.response.IdToken;
import application.line.api.response.Verify;
import application.utils.Client;
import retrofit2.Call;

/**
 * LINE API サービス。
 */
@Service
public class LineAPIService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Value("${line.linelogin.channelId}")
    private String channelId;
    @Value("${line.linelogin.channelSecret}")
    private String channelSecret;
    @Value("${line.linelogin.callbackUrl}")
    private String callbackUrl;

    private static String botChannelToken;

    @Value("${line.bot.channelToken}")
    private void setBotChannelToken(String aBotChannelToken) {
        LineAPIService.botChannelToken = aBotChannelToken;
    }

    private static String botChannelSecret;

    @Value("${line.bot.channelSecret}")
    private void setBotChannelSecret(String aBotChannelSecret) {
        LineAPIService.botChannelSecret = aBotChannelSecret;
    }

    @Autowired
    private AttendanceService attendanceService;

    public AccessToken accessToken(String code) {
        return getClient(t -> t.accessToken(
                GRANT_TYPE_AUTHORIZATION_CODE,
                channelId,
                channelSecret,
                callbackUrl,
                code));
    }

    public AccessToken refreshToken(final AccessToken accessToken) {
        return getClient(t -> t.refreshToken(
                GRANT_TYPE_REFRESH_TOKEN,
                accessToken.refresh_token,
                channelId,
                channelSecret));
    }

    public Verify verify(final AccessToken accessToken) {
        return getClient(t -> t.verify(
                accessToken.access_token));
    }

    public void revoke(final AccessToken accessToken) {
        getClient(t -> t.revoke(
                accessToken.access_token,
                channelId,
                channelSecret));
    }

    public IdToken idToken(String id_token) {
        try {
            DecodedJWT jwt = JWT.decode(id_token);
            return new IdToken(
                    jwt.getClaim("iss").asString(),
                    jwt.getClaim("sub").asString(),
                    jwt.getClaim("aud").asString(),
                    jwt.getClaim("ext").asLong(),
                    jwt.getClaim("iat").asLong(),
                    jwt.getClaim("nonce").asString(),
                    jwt.getClaim("name").asString(),
                    jwt.getClaim("picture").asString());
        } catch (JWTDecodeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLineWebLoginUrl(String state, String nonce, List<String> scopes) {
        final String encodedCallbackUrl;
        final String scope = String.join("%20", scopes);

        try {
            encodedCallbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "https://access.line.me/oauth2/v2.1/authorize?response_type=code"
                + "&client_id=" + channelId
                + "&redirect_uri=" + encodedCallbackUrl
                + "&state=" + state
                + "&scope=" + scope
                + "&nonce=" + nonce;
    }

    public boolean verifyIdToken(String id_token, String nonce) {
        try {
            JWT.require(
                    Algorithm.HMAC256(channelSecret))
                    .withIssuer("https://access.line.me")
                    .withAudience(channelId)
                    .withClaim("nonce", nonce)
                    .build()
                    .verify(id_token);
            return true;
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding not supported
            return false;
        } catch (JWTVerificationException e) {
            // Invalid signature/claims
            return false;
        }
    }

    /**
     * LINEからのPOSTリクエストを処理する。
     * @param req httpリクエスト
     */
    public void requestLinePost(HttpServletRequest req) {
        try {
            LineSignatureValidator vlidator = new LineSignatureValidator(botChannelSecret.getBytes());
            LineBotCallbackRequestParser parser = new LineBotCallbackRequestParser(vlidator);
            CallbackRequest cbRequest = parser.handle(req);
            requestEventList(cbRequest.getEvents());
        } catch (IOException | LineBotCallbackException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * LINEイベントを処理する。
     * @param events LINEイベントリスト
     */
    public void requestEventList(List<Event> events) {
        for (Event event : events) {
            // 受信内容処理
            if (event instanceof MessageEvent) {
                requestEvent((MessageEvent<?>) event);
            } else {
                logger.warn("未知のLINEイベント:" + event);
            }
        }
    }

    /**
     * (メッセージ)LINEイベントを１件処理する。
     * @param evt LINEイベント
     */
    public void requestEvent(MessageEvent<?> evt) {
        MessageContent content = evt.getMessage();
        if (content instanceof TextMessageContent) {
            requestEvent(evt, (TextMessageContent) content);
        } else {
            logger.warn("未知のLINEメッセージイベント:" + evt);
        }
    }

    /**
     * 「String」のLINEメッセージイベントを処理する。
     * @param evt その他のイベント
     * @param message メッセージ内容
     */
    public void requestEvent(MessageEvent<?> evt, TextMessageContent message) {
        String text = message.getText();
        logger.debug("requestEvent(MessageEvent, TextMessageContent) message={}", text);

        // メニュー操作判定
        MenuCd menuCd = MenuCd.getByLineMenuCd(text);
        if (menuCd != null) {
            attendanceService.requestMenu(menuCd, evt, message);
        } else {
            // 文字列操作
            attendanceService.requestText(text, evt, message);
        }
    }

    /**
     * リプライでテキストを送信する。
     * @param replyToken リプライトークン
     * @param msg メッセージ
     * @return LINEのレスポンス
     */
    public static BotApiResponse repryMessage(String replyToken, String msg) {
        try {
            TextMessage textMsg = new TextMessage(msg);
            ReplyMessage replyMessage = new ReplyMessage(replyToken, textMsg);
            LineMessagingClient line = new LineMessagingClientBuilder(botChannelToken).build();
            BotApiResponse lineResponse = line.replyMessage(replyMessage).get();

            logger.debug("LINE Response=[" + lineResponse.toString() + "]");
            return lineResponse;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    private <R> R getClient(final Function<LineAPI, Call<R>> function) {
        return Client.getClient("https://api.line.me/", LineAPI.class, function);
    }

}
