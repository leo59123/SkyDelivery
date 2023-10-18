package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO){


        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);//查询需要的参数封装到实体对象中

        //user参数还没有获取,我们回想一下,登陆时会验证用户token,所以拦截器中是有出现userId的,发现原来装载进了BaseContext类中
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询数据
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        if(list!=null&&!list.isEmpty()) {//已经存在

            //更新菜品数量
            ShoppingCart result=list.get(0);//查出唯一的元素
            result.setNumber(result.getNumber()+1);

            shoppingCartMapper.updateNumberById(result);

        }else{
            //如果不存在,插入到表中,需要区分是一个菜品还是套餐,查询对应的信息来构造购物车对象

            //set  dish/setmeal(name ,image,prive) number=1 , createTime ,Id插入时我们让他自动赋值并返回
            Long dishId= shoppingCartDTO.getDishId();
            if(dishId!=null){
                //表示插入的是dish,查询对应的dish, 获取dish相关的信息用来, 构造ShopoingCart信息
                Dish dish=dishMapper.getById(dishId);

                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                //dishFlavor从对象的属性拷贝获得

            }else {
                //获取并赋值套餐相关信息
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart);

        }



    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> list(){
        ShoppingCart shoppingCart =ShoppingCart.builder()
                .id(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> shoppingCarts=shoppingCartMapper.list(shoppingCart);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     */
    public void clean(){
        Long userId=BaseContext.getCurrentId();
        shoppingCartMapper.DeleteByUserId(userId);
    }
}
