package com.fh.controller;

import com.fh.login.aop.LoginAnnotation;
import com.fh.service.IOrderService;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:8081",maxAge = 3600,exposedHeaders = "NOLOGIN")
@RequestMapping("orders")
public class OrderController {


    @Autowired
    IOrderService orderService;

    @GetMapping
    @LoginAnnotation
    public ResponseServer orderPay(){
        orderService.orderPay();
        return ResponseServer.success(new ArrayList<>());
    }

}
