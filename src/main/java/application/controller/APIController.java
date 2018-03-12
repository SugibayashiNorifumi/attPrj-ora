package application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import application.service.LineAPIService;

/**
 * APIコントローラ
 *
 * @author 作成者氏名
 *
 */
@RestController
public class APIController {

    @Autowired
    private LineAPIService lineAPIService;

    public LineAPIService getLineAPIService() {
        return lineAPIService;
    }

    public void setLineAPIService(LineAPIService lineAPIService) {
        this.lineAPIService = lineAPIService;
    }

}
