package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据 dish id查询套餐 id
     * @param dishIds
     * @return
     */
    //用菜品id查套餐id,多对多关系
    //select setmeal id from setmeal_dish where dish_id in (#{ids})动态sql体现在 ids个数是不确定的
    List<Long > getSetmealIdsByDishIds(List<Long > dishIds);
}
