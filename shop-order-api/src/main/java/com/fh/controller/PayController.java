package com.fh.controller;

import com.fh.login.aop.LoginAnnotation;
import com.fh.service.IPayService;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("pay")
@CrossOrigin(origins = "http://localhost:8081",maxAge = 3600)
public class PayController {

    @Autowired
    IPayService payService;
    @GetMapping("/{outTradeNo}")
    @LoginAnnotation
    public ResponseServer payEWM(HttpServletRequest request,@PathVariable String outTradeNo){
        String phone= (String) request.getAttribute("phone");

        return payService.payEWM(phone,outTradeNo);
    }

    @PostMapping
    @LoginAnnotation
    public ResponseServer checkPayStatus(HttpServletRequest request,String outTradeNo,String orderId){
        String phone= (String) request.getAttribute("phone");
        return payService.checkPayStatus(phone, outTradeNo, orderId);
    }

}
