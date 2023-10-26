package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.stereotype.Service;

import java.util.List;

//接口只是对业务抽象的定义，而不是具体的业务逻辑，定义@Service没有用
public interface OrderService {
    /**
     *
     * @param ordersSubmitDTO
     */
     OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);


    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult historyOrder(int page, int pageSize, Integer status);

    /**
     * 根据id查询订单细节
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;
    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单的分页条件查询
     * @param ordersPageQueryDTO
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();
}
