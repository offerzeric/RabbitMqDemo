package com.eric.producerdemo.controller;

import com.eric.producerdemo.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author ericzhang 2021/11/30 11:40 下午
 */
@RestController
@RequestMapping(path = "/rabbit")
public class Controller {

    @Autowired
    private SendService sendService;

    @RequestMapping(path = "/send")
    public void basicSend(){
        sendService.basicSend();
    }



}
