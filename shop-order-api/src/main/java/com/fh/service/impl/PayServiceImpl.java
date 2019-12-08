package com.fh.service.impl;

import com.fh.bean.Order;
import com.fh.bean.PayLog;
import com.fh.commons.CommonUtil;
import com.fh.config.MyWxConfig;
import com.fh.dao.IOrderDao;
import com.fh.dao.IPayLogDao;
import com.fh.rediskey.RedisKeyUtil;
import com.fh.service.IPayService;
import com.fh.utils.DateUtil;
import com.fh.utils.response.ResponseServer;
import com.fh.utils.response.ServerEnum;
import com.github.wxpay.sdk.WXPay;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements IPayService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IPayLogDao IPayLogDao;
    @Autowired
    IOrderDao orderDao;
    @Override
    public ResponseServer payEWM(String phone, String outTradeNo) {

        PayLog payLog= (PayLog) redisTemplate.opsForHash().get(RedisKeyUtil.getWaitPayKey(phone),outTradeNo);
        if(payLog == null){
            return ResponseServer.error(ServerEnum.NO_ORDER_TO_PAY);
        }
        MyWxConfig myWxConfig=new MyWxConfig();
        try{
            WXPay wxPay=new WXPay(myWxConfig);
            Map<String,String> data=new HashMap<String,String>();
            data.put("body","小田便利店");
            data.put("out_trade_no",outTradeNo);
            data.put("fee_type", "CNY");
            //设置支付的失效时间
            Date date=DateUtils.addMinutes(new Date(),2);
            data.put("time_expire", DateUtil.getYyyyMMhhmmss(date, DateUtil.yyyyMMhhmmss));
            //设置支付的金额
            //BigDecimalUtil.mul(payLog.getPayMoney()+"", "100").intValue();
            int payMoney=1;
            data.put("total_fee", payMoney+"");
            //设置接口的调用路径
            data.put("notify_url", "http://www.example.com/wxpay/notify");
            // 此处指定为扫码支付
            data.put("trade_type", "NATIVE");
            Map<String, String> resulMap = wxPay.unifiedOrder(data);
            //验证接口是否能够正常访问
            String returnCode = resulMap.get("return_code");
            String returnMsg = resulMap.get("return_msg");
            if(!"SUCCESS".equalsIgnoreCase(returnCode)){
                return ResponseServer.error();
            }
            //验证业务是否成功
            String resultCode=resulMap.get("result_code");
            String  errorCodeDes=resulMap.get("err_code_des");
            if(!"SUCCESS".equalsIgnoreCase(resultCode)){
                return ResponseServer.error();
            }
            String url=resulMap.get("code_url");
            Map<String ,Object> map=new HashMap<String ,Object>();
            map.put("codeUrl",url);
            map.put("outTradeNo",outTradeNo);
            System.out.println(outTradeNo);
            map.put("payMoney",payLog.getPayMoney());
            return ResponseServer.success(map);
        }catch (Exception e){
            return ResponseServer.error(ServerEnum.CRATER_PAY_ERROR);

        }

    }

    @Override
    public ResponseServer checkPayStatus(String phone, String outTradeNo, String orderId) {

        //查询对用要支付的订单
        PayLog payLog= (PayLog) redisTemplate.opsForHash().get(RedisKeyUtil.getWaitPayKey(phone),outTradeNo);
        if(payLog == null){
            return ResponseServer.error(ServerEnum.NO_ORDER_TO_PAY);
        }

        MyWxConfig myWxConfig=new MyWxConfig();
        int count=0;
        while (true){
            try{
                WXPay wxPay=new WXPay(myWxConfig);
                Map<String ,String> data=new HashMap<String ,String>();
                data.put("out_trade_no",outTradeNo);
                Map<String, String> resp = wxPay.orderQuery(data);
                //验证接口是否能够正常访问
                String returnCode = resp.get("return_code");
                String returnMsg = resp.get("return_msg");
                if(!"SUCCESS".equalsIgnoreCase(returnCode)){
                    return ResponseServer.error(99999,returnMsg);
                }
                //验证业务是否成功
                String resultCode=resp.get("result_code");
                String  errorCodeDes=resp.get("err_code_des");
                if(!"SUCCESS".equalsIgnoreCase(resultCode)){
                    return ResponseServer.error(99999,errorCodeDes);
                }
                String tradeState=resp.get("trade_state");
                if("SUCCESS".equalsIgnoreCase(tradeState)){
                    //更新支付单的状态
                    PayLog pay=new PayLog();
                    pay.setOutTradeNo(outTradeNo);
                    pay.setPayStatus(CommonUtil.ORDER_STATUS_PAY_SUCCESS);
                    String transactionId=resp.get("transaction_id");
                    pay.setPayTime(new Date());
                    pay.setTransactionId(Integer.parseInt(transactionId));
                    IPayLogDao.updatePay(pay);

                    //更新订单的状态
                    Order order=new Order();
                    order.setId(payLog.getOrderId());
                    order.setPayTime(new Date());
                    order.setStatus(CommonUtil.ORDER_STATUS_PAY_SUCCESS);
                    orderDao.updatePay(order);
                    //删除redis待支付数据
                    redisTemplate.opsForHash().delete(RedisKeyUtil.getWaitPayKey(phone),outTradeNo);
                    return   ResponseServer.success();
                }
                count++;
                Thread.sleep(3000L);
                if(count>50){
                    return ResponseServer.error(ServerEnum.PAY_TIMEOUT);
                }
            }catch (Exception e){
                return ResponseServer.error(ServerEnum.CRATER_PAY_ERROR);
            }
        }
    }
}
