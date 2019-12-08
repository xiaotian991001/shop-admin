package com.fh.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fh.bean.PayLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IPayLogDao{
    void insertInto(PayLog payLog);

    void updatePay(PayLog pay);
}
