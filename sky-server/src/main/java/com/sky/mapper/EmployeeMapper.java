package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
//
//数据库操作
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")//Mybatis通过注解的方式编写了sql，也可以用xml来配置
    //选择方式：复杂、动态sql用xml配置到mapper映射文件中，简单语句用Mybatis
    Employee getByUsername(String username);

}
