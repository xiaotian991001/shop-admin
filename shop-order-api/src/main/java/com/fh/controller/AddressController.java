package com.fh.controller;


import com.fh.bean.AddressBean;
import com.fh.login.aop.LoginAnnotation;
import com.fh.service.IAddressService;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("address")
@CrossOrigin(origins = "http://localhost:8081",maxAge = 3600)
public class AddressController {

    @Autowired
    IAddressService addressService;
    @GetMapping
    @LoginAnnotation
    public ResponseServer queryAddress(HttpServletRequest request){
        String phone = (String) request.getAttribute("phone");
        return addressService.queryAddress(phone);
    }

    @PostMapping
    @LoginAnnotation
    public ResponseServer insertAddress(AddressBean addressBean, HttpServletRequest request){
        String phone = (String) request.getAttribute("phone");
        addressBean.setPhone(phone);
        return addressService.insertAddress(addressBean);
    }

    @DeleteMapping("/{addressId}")
    @LoginAnnotation
    public ResponseServer deleteAddress(@PathVariable Integer addressId){
        addressService.deleteAddress(addressId);
        return ResponseServer.success();
    }
}
