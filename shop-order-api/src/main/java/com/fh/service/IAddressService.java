package com.fh.service;

import com.fh.bean.AddressBean;
import com.fh.utils.response.ResponseServer;

public interface IAddressService {
    ResponseServer queryAddress(String phone);

    ResponseServer insertAddress(AddressBean addressBean);

    void deleteAddress(Integer addressId);

    ResponseServer byOneAddress(Integer addressId);

    ResponseServer upGetAddress(Integer id);

    void updateAddress(AddressBean address);
}
