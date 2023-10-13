package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/shop")
@RestController("adminShopController")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 设置店铺状态
     * @param status
     * @return
     */
    @ApiOperation("设置店铺的营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable  Integer status){//地址栏传参的方式来设置
        log.info("设置店铺状态:{}",status==1?"营业中":"打烊中");
        //利用redis存储<SHOP_STATUS,1=营业中\0=打烊>, 我们选择简单的Value类型就可,模板属性自动注入
//        redisTemplate.opsForValue().set("SHOP_STATUS",status);
        redisTemplate.opsForValue().set(KEY,status); //改为常量字符串来指定
        return Result.success();
    }

    /**
     * 从redis中取出状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("查询到店铺状态:{}",shopStatus==1?"营业中":"打烊中");
        return Result.success(shopStatus);
    }


}
