package application.controller;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.linecorp.bot.model.profile.UserProfileResponse;

import application.entity.MUser;
import application.form.LoginForm;
import application.line.api.response.AccessToken;
import application.line.api.response.IdToken;
import application.service.LineAPIService;
import application.service.UserService;
import application.utils.CommonUtils;
import application.utils.LineUtils;

/**
 * 一般ユーザ向け機能用 画面コントローラ.
 * @author 作成者氏名
 */
@Controller
@RequestMapping(value = "/user")
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    //    private static final String USER_MAIL = "userEmail";
    //    private static final String LINE_WEB_LOGIN_STATE = "lineWebLoginState";
    static final String ACCESS_TOKEN = "accessToken";
    private static final String NONCE = "nonce";

    @Value("${line.bot.lineAtId}")
    private String lineAtId;

    @Autowired
    private UserService userService;

    @Autowired
    private LineAPIService lineAPIService;

    @Autowired
    private HttpSession httpSession;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(LoginForm loginForm) {
        return "user/login";
    }

    @RequestMapping(value = "/linelogin", method = RequestMethod.POST)
    public String goToAuthPage(@ModelAttribute(value = "loginForm") @Valid LoginForm loginForm, BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "user/login";
        }

        if (!userService.getUserByMail(loginForm.mail).isPresent()) {
            model.addAttribute("errorMessage", "メールアドレスが存在しません");
            return "user/login";
        }

        logger.debug("mail address :" + loginForm.mail);

        final String state = loginForm.mail;
        final String nonce = CommonUtils.getToken();
        //        httpSession.setAttribute(LINE_WEB_LOGIN_STATE, state);
        //        httpSession.setAttribute(NONCE, nonce);
        //        httpSession.setAttribute(USER_MAIL, loginForm.mail);

        //        logger.info(
        //                "【httpSession.getAttribute(LINE_WEB_LOGIN_STATE) 】:" + httpSession.getAttribute(LINE_WEB_LOGIN_STATE));

        final String url = lineAPIService.getLineWebLoginUrl(state, nonce, Arrays.asList("openid", "profile"));
        return "redirect:" + url;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String auth(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "errorCode", required = false) String errorCode,
            @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        if (logger.isDebugEnabled()) {
            logger.debug("parameter code : " + code);
            logger.debug("parameter state : " + state);
            logger.debug("parameter scope : " + scope);
            logger.debug("parameter error : " + error);
            logger.debug("parameter errorCode : " + errorCode);
            logger.debug("parameter errorMessage : " + errorMessage);
        }

        // パラメータチェック
        if (error != null || errorCode != null || errorMessage != null) {
            return "redirect:/user/loginCancel";
        }

        logger.debug("【mail=state】:" + state);
        String mail = state;

        if (StringUtils.isEmpty(mail)) {
            return "redirect:/user/loginError";
        }

        Optional<MUser> userOpt = userService.getUserByMail(mail);
        if (!userOpt.isPresent()) {
            return "redirect:/user/loginError";
        }
        Integer userId = userOpt.get().getUserId();

        // LINEログイン情報取得
        AccessToken token = lineAPIService.accessToken(code);
        UserProfileResponse profile = LineUtils.getProfileByAccessToken(token.access_token);
        String lineId = profile.getUserId();
        logger.debug("【userId -> lineId】:{} -> {}", userId, lineId);

        if (logger.isDebugEnabled()) {
            logger.debug("scope : " + token.scope);
            logger.debug("access_token : " + token.access_token);
            logger.debug("token_type : " + token.token_type);
            logger.debug("expires_in : " + token.expires_in);
            logger.debug("refresh_token : " + token.refresh_token);
            logger.debug("id_token : " + token.id_token);
        }

        httpSession.setAttribute(ACCESS_TOKEN, token);
        logger.debug("access token: " + token);
        //
        //        final IdToken idToken = lineAPIService.idToken(token.id_token);
        //
        //        logger.debug("id token: " + idToken);

        userService.registerLineId(userId, lineId);

        return "redirect:/user/success";
    }

    @RequestMapping("/success")
    public String success(Model model) {
        logger.debug("【success start】:");
        AccessToken token = (AccessToken) httpSession.getAttribute(ACCESS_TOKEN);
        if (token == null) {
            logger.debug("【success A】:");
            return "redirect:/";
        }

        httpSession.removeAttribute(NONCE);
        IdToken idToken = lineAPIService.idToken(token.id_token);
        if (logger.isDebugEnabled()) {
            logger.debug("userId : " + idToken.sub);
            logger.debug("displayName : " + idToken.name);
            logger.debug("pictureUrl : " + idToken.picture);
        }
        model.addAttribute("idToken", idToken);

        //        UserProfileResponse channelProfile = LineUtils.getProfileByAccessToken(channelToken);
        model.addAttribute("lineAtId", lineAtId);
        //        logger.debug("pictureUrl : " + channelProfile.getPictureUrl());

        logger.debug("【success C】:");
        return "user/add_friend";
    }

    @RequestMapping("/loginCancel")
    public String loginCancel() {
        return "user/login_cancel";
    }

    @RequestMapping("/sessionError")
    public String sessionError() {
        return "user/session_error";
    }

}
