package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据信息查询购物车内是否存在
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);//因为根据菜品和套餐区分查询类型,所以我们需要生成动态sql

    /**
     * 修改购物车中信息的数量
     * @param result
     */
    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart result);

    /**
     * 插入购物车信息
     * @param shoppingCart
     */
    @Insert("INSERT INTO shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time)"+
           "values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})" )
    void insert(ShoppingCart shoppingCart);
}
