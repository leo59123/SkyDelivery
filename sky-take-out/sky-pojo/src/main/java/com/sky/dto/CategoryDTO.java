package com.sky.dto;
//DTO:数据传输对象，通常用于程序中各层之间传递数据

import lombok.Data;

import java.io.Serializable;

@Data  //lombok 工具库，省去大量的set\get 方法，代码更简洁
public class CategoryDTO implements Serializable {

    //主键
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    //分类名称
    private String name;

    //排序
    private Integer sort;

}
