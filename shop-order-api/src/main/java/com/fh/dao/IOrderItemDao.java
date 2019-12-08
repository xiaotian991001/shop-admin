package com.fh.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fh.bean.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IOrderItemDao extends BaseMapper<OrderItem> {
    void insertInto(OrderItem orderItem);
}
