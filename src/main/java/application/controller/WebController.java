package application.controller;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import application.entity.MUser;
import application.form.LoginForm;
import application.line.api.response.AccessToken;
import application.line.api.response.IdToken;
import application.service.LineAPIService;
import application.service.UserService;
import application.utils.CommonUtils;

/**
 * <p>user web application pages</p>
 */
@Controller
@RequestMapping(value = "/user")
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

	private static final String USER_MAIL = "userEmail";
    private static final String LINE_WEB_LOGIN_STATE = "lineWebLoginState";
    static final String ACCESS_TOKEN = "accessToken";
    private static final String NONCE = "nonce";

    @Autowired
    private UserService userService;

    @Autowired
    private LineAPIService lineAPIService;

    @Autowired
    private HttpSession httpSession;

    /**
     * <p>LINE Login Button Page
     * <p>Login Type is to log in on any desktop or mobile website
     */
    @RequestMapping(value = "/login", method=RequestMethod.GET)
    public String login(LoginForm loginForm) {
        return "user/login";
    }

    /**
     * <p>Redirect to LINE Login Page</p>
     */
    @RequestMapping(value = "/linelogin", method=RequestMethod.POST)
    public String goToAuthPage(
            @ModelAttribute(value = "loginForm") @Valid LoginForm loginForm,
            BindingResult result,
            Model model) {

        if(result.hasErrors()) {
            return "user/login";
        }

        if(!userService.getUserByMail(loginForm.mail).isPresent()) {
            model.addAttribute("errorMessage", "メールアドレスが存在しません");
            return "user/login";
        }

        logger.debug("mail address :" + loginForm.mail);

        final String state = CommonUtils.getToken();
        final String nonce = CommonUtils.getToken();
        httpSession.setAttribute(LINE_WEB_LOGIN_STATE, state);
        httpSession.setAttribute(NONCE, nonce);
        httpSession.setAttribute(USER_MAIL, loginForm.mail);

        logger.info("【httpSession.getAttribute(LINE_WEB_LOGIN_STATE) 】:" + httpSession.getAttribute(LINE_WEB_LOGIN_STATE));

        final String url = lineAPIService.getLineWebLoginUrl(state, nonce, Arrays.asList("openid", "profile"));
        return "redirect:" + url;
    }

    /**
     * <p>Redirect Page from LINE Platform</p>
     * <p>Login Type is to log in on any desktop or mobile website
     */
    @RequestMapping(value = "/auth", method=RequestMethod.GET)
    public String auth(
            @RequestParam(value = "code", required = false) String code,
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

        if (error != null || errorCode != null || errorMessage != null){
            return "redirect:/user/loginCancel";
        }

        logger.info("【state 】:" + state);
        logger.info("【httpSession.getAttribute(LINE_WEB_LOGIN_STATE) 】:" + httpSession.getAttribute(LINE_WEB_LOGIN_STATE));

        if (!state.equals(httpSession.getAttribute(LINE_WEB_LOGIN_STATE))){
            return "redirect:/user/sessionError";
        }

        final String mail = (String) httpSession.getAttribute(USER_MAIL);

        logger.debug("mail address :" + mail);

        if(StringUtils.isEmpty(mail)) {
            return "redirect:/user/loginError";
        }

        Optional<MUser> userOpt = userService.getUserByMail(mail);

        if(!userOpt.isPresent()) {
            return "redirect:/user/loginError";
        }

        httpSession.removeAttribute(USER_MAIL);
        httpSession.removeAttribute(LINE_WEB_LOGIN_STATE);

        AccessToken token = lineAPIService.accessToken(code);
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

        final IdToken idToken = lineAPIService.idToken(token.id_token);

        logger.debug("id token: " + idToken);

        userService.registerLineId(userOpt.get().userId, idToken.sub);

        return "redirect:/user/success";
    }

    /**
    * <p>login success Page
    */
    @RequestMapping("/success")
    public String success(Model model) {

        AccessToken token = (AccessToken)httpSession.getAttribute(ACCESS_TOKEN);
        if (token == null){
            return "redirect:/";
        }

        if (!lineAPIService.verifyIdToken(token.id_token, (String) httpSession.getAttribute(NONCE))) {
            // verify failed
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
        return "user/add_friend";
    }

    /**
    * <p>login Cancel Page
    */
    @RequestMapping("/loginCancel")
    public String loginCancel() {
        return "user/login_cancel";
    }

    /**
    * <p>Session Error Page
    */
    @RequestMapping("/sessionError")
    public String sessionError() {
        return "user/session_error";
    }

}
