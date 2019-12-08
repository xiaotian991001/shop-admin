package com.fh.service;

import com.fh.utils.response.ResponseServer;

public interface IPayService {
    ResponseServer payEWM(String phone, String outTradeNo);

    ResponseServer checkPayStatus(String phone, String outTradeNo, String orderId);
}
