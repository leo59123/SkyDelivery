package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.impl.SetmealServiceImpl;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealServiceImpl setmealServiceImpl;

    @PostMapping()
    @ApiOperation("新增套餐")

    @Cacheable(cacheNames = "setmealCache",key="#setmealDTO.categoryId")//key=setmeal::10
    public Result save(@RequestBody SetmealDTO setmealDTO){//请求参数为json
        log.info("新增套餐：{}",setmealDTO);
        setmealServiceImpl.saveWithDishs(setmealDTO);
        return Result.success();

    }
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询:{}",setmealPageQueryDTO);
        PageResult pageResult= setmealServiceImpl.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids ){
        log.info("菜品批量删除:{}",ids);
        setmealServiceImpl.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据 查询套餐
     * @param id
     * @return
     */
    //修改套餐实现
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        SetmealVO setmealVO = setmealServiceImpl.getById(id);
        return Result.success(setmealVO);
    }
    @PutMapping
    @ApiOperation("修改套餐")

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐:{}",setmealDTO);
        setmealServiceImpl.updateWithDishes(setmealDTO  );
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")//参考员工的启用和禁用

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setmealServiceImpl.startOrStop(status, id);
        return Result.success();
    }
}
