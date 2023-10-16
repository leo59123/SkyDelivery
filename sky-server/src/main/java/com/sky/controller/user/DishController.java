package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        //优先查询redis
        //生成key"dish_i"
        String key="dish_"+categoryId;

        //通过注入模板类，来获取管理redis的对象,然后调用get方法，返回为redis的string对象，我们可以强制类型转换为自己需要的类型
        List<DishVO> listRedis = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //判断是否存储在缓存中
        if(listRedis!=null&&!listRedis.isEmpty()){
            //如果存在,直接从缓存中返回
            return Result.success(listRedis );
        }
        //不存在
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);

        //需要写入缓存加速下次访问
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }

}
