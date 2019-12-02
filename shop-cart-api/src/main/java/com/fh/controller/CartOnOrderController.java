package com.fh.controller;

import com.fh.login.aop.LoginAnnotation;
import com.fh.service.ICartService;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("cartOnOrder")
@CrossOrigin(origins = "http://localhost:8081",maxAge = 3600)
public class CartOnOrderController {

    @Autowired
    ICartService cartService;

    @GetMapping
    @LoginAnnotation
    public ResponseServer queryCartsChecked(HttpServletRequest request){
        String phone = (String) request.getAttribute("phone");

        return cartService.queryCartsChecked(phone);
    }
}
