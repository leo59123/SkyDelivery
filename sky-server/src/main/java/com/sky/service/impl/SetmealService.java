package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealService implements com.sky.service.SetmealService {
    @Autowired
    private SetmealMapper setmealMapper ;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    public void saveWithDishs(SetmealDTO setmealDTO) {

        Setmeal setmeal =new Setmeal();//用实体先保存套餐相关信息
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向setmeal表插入套餐数据
        setmealMapper.insert(setmeal);

        List<SetmealDish> dishs= setmealDTO.getSetmealDishes();
        //额外的list 字段 向 setmealdish 表 批量插入
        if(dishs!=null&& dishs.size()>0 ){
            //填充SetMealDish 实体中空缺的 dishId
            Long setmealId=setmeal.getId();//套餐ID是刚才插进去后才获得的,因此setmealId要取得并给套餐内的菜品赋值
            for (SetmealDish dish : dishs) {
                dish.setSetmealId(setmealId);
            }
            //实现insert into setmeal_dish values( , , , ...)
            setmealDishMapper.insertBatch(dishs);
        }
    }
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //利用PageHelper插件来处理,传入pageNum和pageSize就可以自动计算并执行分页查询
        // 实际上底层也是ThreadLocal对象，然后到了 执行sql之前，就从中取出参数，在动态sql中填入参数，拼上limit然后执行
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());//返回全部数量和record交给前端
    }
    /**
     * 套餐批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前套餐是否启售--> 遍历,根据id查询套餐,检验status
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //套餐即使和菜品有关联也可以直接删除套餐,
        setmealMapper.deleteBatch(ids);
        //并且要删除setmeal_dish表中的 id对应的关系:注意即使ids非空,关系也有可能是空表,全都是空套餐
        setmealDishMapper.deleteBySetmealIds(ids);


    }
}
