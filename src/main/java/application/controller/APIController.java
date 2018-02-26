package application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import application.service.LineAPIService;

@RestController
public class APIController {

    @Autowired
    private LineAPIService lineAPIService;

}
