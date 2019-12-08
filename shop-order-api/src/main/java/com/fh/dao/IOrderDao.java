package com.fh.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fh.bean.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IOrderDao extends BaseMapper<Order> {

    Integer updateProductStock(@Param("productId") int productId, @Param("count") Integer count);

    void insertInto(Order order);

    void updatePay(Order order);
}
