package com.fh.service.impl;

import com.fh.bean.AddressBean;
import com.fh.dao.IAddressDao;
import com.fh.service.IAddressService;
import com.fh.utils.response.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class AddressService implements IAddressService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IAddressDao addressDao;
    @Override
    public ResponseServer queryAddress(String phone) {

        List<AddressBean> addressList=addressDao.queryAddress(phone);

        return ResponseServer.success(addressList);
    }

    @Override
    public ResponseServer insertAddress(AddressBean addressBean) {
        addressBean.setUpdateTime(new Date());
        addressDao.insertAddress(addressBean);

        return ResponseServer.success();
    }

    @Override
    public void deleteAddress(Integer addressId) {
        addressDao.deleteAddress(addressId);
    }

    @Override
    public ResponseServer byOneAddress(Integer addressId) {

        return ResponseServer.success(addressDao.byOneAddress(addressId));
    }

    @Override
    public ResponseServer upGetAddress(Integer id) {
        AddressBean address=addressDao.byOneAddress(id);
        return ResponseServer.success(address);
    }

    @Override
    public void updateAddress(AddressBean address) {
        address.setUpdateTime(new Date());
        addressDao.updateAddress(address);
    }
}
