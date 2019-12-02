package com.fh.controller;

import com.fh.login.aop.LoginAnnotation;
import com.fh.service.ICartService;
import com.fh.utils.jwt.JwtUtils;
import com.fh.utils.response.ResponseServer;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/carts")
@CrossOrigin(maxAge = 3600,origins = "http://localhost:8081",exposedHeaders="NOLOGIN")
public class CartController {

    @Autowired
    private ICartService cartService;
    @PostMapping
    @LoginAnnotation
    public ResponseServer addCart(Integer productId, HttpServletRequest request){
        String userPhone= (String) request.getAttribute("phone");
        return cartService.addCart(productId,userPhone);
    }
    @GetMapping
    @LoginAnnotation
    public ResponseServer cartAll(){

        return ResponseServer.success();
    }
    @GetMapping("queryCartCount")
    public ResponseServer queryCartCount(HttpServletRequest request){

        String token =request.getHeader("token");
        ResponseServer responseServer = JwtUtils.resolverToken(token);
        Claims claims= (Claims) responseServer.getData();
        String phone= (String) claims.get("phone");
        Long count=cartService.queryCartCount(phone);
        System.out.println(count);
        return ResponseServer.success(count);
    }

}