package application.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import application.service.LineAPIService;

/**
 * APIコントローラ。
 * @author 作成者氏名
 */
@RestController
@RequestMapping(value = "api/")
public class APIController {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    /** レスポンス型定義：JSON。 */
    public static final String PRODUCES_JSON = "application/json; charset=UTF-8";

    /** LINE API サービス。 */
    @Autowired
    private LineAPIService lineAPIService;

    /**
     * LineのWebhook受信。
     * @param model モデル
     * @param req httpリクエスト
     * @return 受信結果
     */
    @RequestMapping(value = "line", method = { RequestMethod.POST }, produces = PRODUCES_JSON)
    @ResponseBody
    public String lineWebhook(Model model, HttpServletRequest req) {
        lineAPIService.requestLinePost(req);
        return "{status: \"OK\"}";
    }

}
