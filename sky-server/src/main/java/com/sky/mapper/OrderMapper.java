package com.sky.mapper;


import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper //注意这是一个接口（因为Mybatis要求Mapper接口需要对数据访问逻辑进行抽象），并且需要添加注解，因为可以依赖注入来管理
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);
}
