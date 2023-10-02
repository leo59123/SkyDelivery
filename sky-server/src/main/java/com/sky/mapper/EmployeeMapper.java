package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("insert into employee (name,username,password,phone,sex,id_number,create_time,update_time,create_user,update_user,status)" +
           "values" +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    //调用注解 insert语句来插入除 ID以外的数据，ID等待后续获得
    void insert(Employee employee);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    //此时因为涉及到动态的page号，因此用单条sql就不太方便了，我们用Mybatis配置文件在yml中配置查询位置，然后在mapper/*.xml中配置sql
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 根据主键动态修改属性
     * @param employee
     */
    void update(Employee employee);//在mapper/*.xml中配置动态sql
}
