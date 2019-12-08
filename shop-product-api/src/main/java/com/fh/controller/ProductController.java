package com.fh.controller;


import com.fh.bean.ProductBean;
import com.fh.bean.ProductParamter;
import com.fh.service.IProductService;
import com.fh.utils.page.PageBean;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@CrossOrigin(maxAge = 3600,origins = "http://localhost:8081")
public class ProductController {

    @Autowired
    IProductService productService;

    @PostMapping
    public PageBean<ProductBean> queryProductList(PageBean page, ProductParamter paramter){

        page=productService.queryProductPageList(page,paramter);
        return page;
    }
    @GetMapping("/{productId}")
    public ResponseServer productStock(@PathVariable Integer productId){

        return productService.productStock(productId);
    }


}

