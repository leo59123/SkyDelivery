package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;

import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //根据开始结束日期拼凑出x轴:begin~end之间的每天日期
        List<LocalDate> dateList= new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }


        //查询订单表,对应状态的
        List<Double> turnoverList= new ArrayList<>();
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        for(LocalDate date:dateList){
            //select sum(amount) from orders where order_time > start and order_time < ? and status = 1
            //获取查询起始:一天的开始
            LocalDateTime startOfDate = LocalDateTime.of(date, LocalTime.MIN);//起始时间为date当天的00:00
            LocalDateTime endOfDate = LocalDateTime.of(date,LocalTime.MAX);
            //查表: 多条件查询
            Map map =new HashMap();
            map.put("start",startOfDate);
            map.put("end",endOfDate);
            map.put("status",Orders.CANCELLED);//这里我们统计待付款的,因为没有支付

            Double turnover= orderMapper.sumByMap(map);
            turnover= turnover==null ? 0.0:turnover;
            turnoverList.add(turnover   );
        }


        return TurnoverReportVO.builder()
                .turnoverList(StringUtil.join(",", turnoverList))
                .dateList(StringUtil.join(",", dateList))//将集合的元素取出来,转换为字符串,拼起来,用,分割
                .build();
    }

    /**
     * 统计指定时间范围内的用户数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //核心目标是拼出VO

        //得到x轴:
        List<LocalDate> dateList= new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }


        //统计每天新增用户量 :两边限制为一天的最大值和最小值
        List<Integer> newUserList =new ArrayList<>();
        //select COUNT(id) from user where crete_time> start and crete_time < ?

        //统计每天总用户的数量: 只限制右边为 end的最小值
        List<Integer> totalUserList =new ArrayList<>();
        //select COUNT(id) from user where crete_time < ?

        for(LocalDate date:dateList){
            //先得到begin和end
            LocalDateTime startOfDate = LocalDateTime.of(date, LocalTime.MIN) ;// 给出date以及具体的时分秒
            LocalDateTime endOfDate = LocalDateTime.of(date,LocalTime.MAX);
            Map map=new HashMap();
            map.put("end",endOfDate);
            //总用户数量
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin",startOfDate);
            //新增用户数量
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtil.join(",",dateList))
                .newUserList(StringUtil.join(",",newUserList))
                .totalUserList(StringUtil.join(",",totalUserList))
                .build();
    }


}
