package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/shop")
@RestController("userShopController")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {//用户端不能修改店铺状态,同时bean的名称不能相同,否则会相同
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 从redis中取出状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("查询到店铺状态:",shopStatus==1?"营业中":"打烊中");
        return Result.success();
    }


}
