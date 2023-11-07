package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper //注意这是一个接口（因为Mybatis要求Mapper接口需要对数据访问逻辑进行抽象），并且需要添加注解，因为可以依赖注入来管理
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    @Select("select count(* ) from orders where status=#{status}")
    Integer countStatus(Integer status);

    /**
     * 查询超时订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status =#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据多个条件查询营业额数据
     * @return
     */
    Double sumByMap(Map map);


    /**
     * 统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map );


    /**
     * 统计指定时间区间内的销量排名
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10( LocalDateTime begin ,LocalDateTime end);
}
