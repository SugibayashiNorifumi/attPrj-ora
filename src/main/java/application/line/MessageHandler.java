package application.line;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.extern.slf4j.Slf4j;
import retrofit2.http.GET;

@Slf4j
@LineMessageHandler
public class MessageHandler {

    @Value("${line.bot.channelToken}")
    private String channelToken;
    @Value("${line.bot.channelSecret}")
    private String channelSecret;

    private static final String MENU_ARRIVAL = "Arrival";
    private static final String MENU_CLOCKOUT = "Clock-out";
    private static final String MENU_REWRITING = "Rewriting";
    private static final String MENU_LISTOUTPUT = "ListOutput";

    /**
     * テキストのLINEメッセージを受け取るイベントハンドラ
     *
     * @param event
     * @return
     */
    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {

        // ReplyMessage ret = null;

        System.out.println("event: " + event);

        // ユーザーがBOTに送信したメッセージ
        TextMessageContent userMessage = event.getMessage();

        String temp = "";
        if (userMessage.getText().equals(MENU_ARRIVAL)) {
            temp = "【出勤】ですね。";
        } else if (userMessage.getText().equals(MENU_CLOCKOUT)) {
            temp = "【退勤】ですね。";
        } else if (userMessage.getText().equals(MENU_REWRITING)) {
            temp = "【修正】ですね。";
        } else if (userMessage.getText().equals(MENU_LISTOUTPUT)) {
            temp = "【リスト】ですね。";
        }

        // ユーザーのProfileを取得する
        UserProfileResponse userProfile = getUserProfile(event.getSource().getUserId());

        // BOTからの返信メッセージ
        String botResponseText = userProfile.getDisplayName() + "さん、" + "ユーザIDは [" + userProfile.getUserId() + "] です。"
                + "「" + userMessage.getText() + "」って言いましたね。。。" + temp;

        TextMessage textMessage = new TextMessage(botResponseText);

        // ret = ReplyMessage(event.getReplyToken(), textMessage.getText());

        return textMessage;
    }

    @EventMapping
    public TextMessage handlePostBackEvent(PostbackEvent event) {
        // ButtonsTemplateでユーザーが選択した結果が、このPostBackEventとして返ってくる

        PostbackContent postbackContent = event.getPostbackContent();

        // PostbackActionで設定したdataを取得する
        String data = postbackContent.getData();

        log.debug("PostbackAction data: {}", data);

        final String replyText;

        if ("japanese".equals(data)) {
            replyText = "和食がお好きなんですね。";
        } else if ("italian".equals(data)) {
            replyText = "イタリアン、良いですよね。";
        } else {
            replyText = "フレンチ、私も食べたいです。";
        }

        //return new TextMessage(event.getReplyToken(), Arrays.asList(new TextMessage(replyText)));

        TextMessage textMessage = new TextMessage(replyText);

        return textMessage;
    }



    /**
     * LINEユーザIDからLINEプロフィール情報を取得する.
     * @param userId LINEユーザID
     * @return UserProfileResponse LINEプロフィール情報
     */
    @GET
    public UserProfileResponse getUserProfile(String userId) {
        UserProfileResponse ret = null;

        final LineMessagingClient client = LineMessagingClient.builder(channelToken).build();

        final UserProfileResponse userProfileResponse;

        try {
            userProfileResponse = client.getProfile(userId).get();

            ret = new UserProfileResponse(userProfileResponse.getDisplayName(), userProfileResponse.getUserId(),
                    userProfileResponse.getPictureUrl(), userProfileResponse.getPictureUrl());

        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return ret;

    }

}
