package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;//由dishId查询风味数据
    @Autowired
    private SetmealDishMapper setmealDishMapper;//由dishId查询套餐信息


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


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);//分页查询操作

        return new PageResult(page.getTotal(),page.getResult());//从插件 分页查询返回的对象中得到响应信息的对象
    }


    /**
     * 菜品的批量删除
     * @param ids
     */

    @Transactional //Spring Boot使用TransactionManager来创建一个新的事务或加入一个已有的事务。来确保一致性
    public void deleteBatch(List<Long> ids){
        //判断当前菜品能否被删除--是否启售中 -> 遍历ids,查询每个菜品的status判断是否启售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);//简单的根据主键查询
            if(dish.getStatus()== StatusConstant.ENABLE) {//用常量类判断
                // 处于启售中,不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);//抛出定义的常量信息
            }
        }

        //判断能否被删除--是否被关联了 -> 根据dishid查询 菜品-套餐表 能否查到,因此需要setmealDishMapper来操作
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds!=null&& setmealIds.size()!=0){
            //当前菜品被关联,不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }



//        //删除菜品表中菜品数据 -> 根据dish id删除
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据  ->删完菜不查了直接 用dishid尝试删除就行了
//            dishFlavorMapper.deleteBydishId(id);
//        }

        // 进一步优化改造: 删除菜品和口味时每次循环都发送了两条sql来执行,有可能对性能产生影响,现在优化为 2条s批量sql直接处理
        //由dishId批量删除菜品数据
        dishMapper.deleteByIds(ids);//delete from dish where id in (, , ,)
        //由dishId批量删除口味数据
        dishFlavorMapper.deleteBydishIds(ids);//delete from dish_flavor where dishId in (, , ,)

    }


    /**
     * 根据id查询菜品和对应的口味
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id){
        //查询菜品表
        Dish dish = dishMapper.getById(id);

        //查询口味表
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDishId(id);

        //封装到VO中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }


    /**
     * 修改菜品及口味
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO){
        //修改菜品表:直接update语句
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);


        //修改口味:先清空当前口味,再重新插入,避免繁琐的修改
        //先删除原有口味
        dishFlavorMapper.deleteBydishId(dishDTO.getId());


        //重新插入,可以直接复用新增菜品的部分
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            //批量插入前给每一项的dishId赋值
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });

            //第一想法是遍历list然后一条条插入,但其实我们可以用mapper写批量插入
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
