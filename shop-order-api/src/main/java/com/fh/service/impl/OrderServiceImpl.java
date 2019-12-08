package com.fh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fh.bean.*;
import com.fh.commons.CommonUtil;
import com.fh.dao.IOrderDao;
import com.fh.dao.IOrderItemDao;
import com.fh.dao.IPayLogDao;
import com.fh.rediskey.RedisKeyUtil;
import com.fh.service.IOrderService;
import com.fh.utils.IdUtil;
import com.fh.utils.httpclient.HttpConnection;
import com.fh.utils.response.ResponseServer;
import com.fh.utils.response.ServerEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderDao orderDao;
    @Autowired
    private IOrderItemDao orderItemDao;
    @Autowired
    private IPayLogDao payLogDao;

    @Override
    public ResponseServer createOrder(Integer addressId,String phone) {
        String cartId = (String) redisTemplate.opsForValue().get("cartid_" + phone);
        LoginBean login = (LoginBean) redisTemplate.opsForValue().get("user_"+phone);
        List<CartBean> cartList=redisTemplate.opsForHash().values(cartId);
        List<CartBean> payCartList = cartList.stream().filter(cart -> cart.getIsChecked()).collect(Collectors.toList());
        List<CartBean> understockList=new ArrayList<CartBean>();
        List<Integer>  havePayList=new ArrayList<Integer>();
        String orderId=IdUtil.getOrderId();
        BigDecimal totalPrice=new BigDecimal(0.00);
        BigDecimal totalCount=new BigDecimal(0);

        for(CartBean cart:payCartList){
            Integer productId=cart.getProductId();
            String url = "http://localhost:8092/products/" + productId;
            String result = HttpConnection.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            JSONObject productData = JSON.parseObject(jsonObject.get("data").toString());
            int stock=productData.getIntValue("stock");
            if(cart.getCount()>stock){
                understockList.add(cart);
            }else {
                Integer successCount= orderDao.updateProductStock(productId,cart.getCount());
                if(successCount == 0){
                    understockList.add(cart);
                }else {
                    totalPrice=totalPrice.add(cart.getSubtotal());
                    totalCount=totalCount.add(new BigDecimal(cart.getCount()));
                    OrderItem orderItem=new OrderItem();
                    orderItem.setCount(Long.valueOf(String.valueOf(cart.getCount())));
                    orderItem.setImage(cart.getMainImg());
                    orderItem.setProductName(cart.getProductName());
                    orderItem.setPrice(cart.getPrice());
                    orderItem.setSubTotalPrice(cart.getSubtotal());
                    orderItem.setProductId(Long.valueOf(String.valueOf(cart.getProductId())));
                    orderItem.setUserId(Long.valueOf(String.valueOf(login.getId())));
                    orderItem.setOrderId(orderId);
                    orderItemDao.insertInto(orderItem);
                    havePayList.add(cart.getProductId());
                }
            }
        }
        //有可能你买的商品库存都不足
        if(totalCount.longValue() == 0){
            return ResponseServer.error(ServerEnum.ALL_STOCK_NULL);
        }
        Order order=new Order();
        order.setAddressId(Long.valueOf(String.valueOf(addressId)));
        order.setId(orderId);
        order.setCreateTime(new Date());
        order.setPayType(1);
        order.setStatus(CommonUtil.ORDER_STATUS_WAIT_PAY);
        order.setUserId(Long.valueOf(String.valueOf(login.getId())));
        order.setTotalCount(totalCount.longValue());
        order.setTotalPrice(totalPrice);
        orderDao.insertInto(order);
        PayLog payLog=new PayLog();
        payLog.setCreateTime(new Date());
        payLog.setOrderId(orderId);
        payLog.setOutTradeNo(IdUtil.getOrderId());
        payLog.setPayMoney(totalPrice);
        payLog.setPayStatus(CommonUtil.ORDER_STATUS_WAIT_PAY);
        payLog.setPayType(1);
        payLog.setUserId(Long.valueOf(String.valueOf(login.getId())));
        payLogDao.insertInto(payLog);
        redisTemplate.opsForHash().put(RedisKeyUtil.getWaitPayKey(phone),payLog.getOutTradeNo(),payLog);
        for(Integer productId:havePayList){
            redisTemplate.opsForHash().delete(cartId,productId);
        }

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("outTradeNo",payLog.getOutTradeNo());
        map.put("orderId",orderId);
        map.put("understockList",understockList);
        return  ResponseServer.success(map);
    }
}
