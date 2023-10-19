package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.stereotype.Service;

//接口只是对业务抽象的定义，而不是具体的业务逻辑，定义@Service没有用
public interface OrderService {
    /**
     *
     * @param ordersSubmitDTO
     */
     OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
