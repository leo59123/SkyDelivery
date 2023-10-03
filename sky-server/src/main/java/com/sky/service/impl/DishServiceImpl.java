package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavour(DishDTO dishDTO) {
        //向菜品表插入数据

        Dish dish = new Dish();//DTO包括dish实体的属性以及 口味列表的属性,此处插入仅需要dish相关数据就可
        BeanUtils.copyProperties(dishDTO,dish);//两个对象的属性命名一致才可以拷贝
        dishMapper.insert(dish);
        //获取insert 语句生成的主键值
        Long dishId=dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            //批量插入前给每一项的dishId赋值
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            //第一想法是遍历list然后一条条插入,但其实我们可以用mapper写批量插入
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
