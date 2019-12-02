package com.fh.dao;

import com.fh.bean.AddressBean;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface IAddressDao {
    List<AddressBean> queryAddress(String phone);

    void insertAddress(AddressBean addressBean);

    void deleteAddress(Integer addressId);
}
