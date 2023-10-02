package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")//使用swqgger注解使得文档具有更好的可读性
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired//将封装好的配置类对象 注解自动注入
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);
        //若登录成功，返回数据库中查询的对象，有问题会在login函数中抛出异常

        //登录成功后，生成jwt 令牌token:为前端使用
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());//token的主要内容是这个map对象，放的是一个常量类的字符串key和 用户的主键值id
        String token = JwtUtil.createJWT(//利用工具类生成token
                jwtProperties.getAdminSecretKey(),//实际上是一个配置属性类，位于common/peoperties配置属性类，将.yml的配置传递过来
                jwtProperties.getAdminTtl(),
                claims);
        //将请求到的数据封装为vo，返回给前端页面
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()//构建器封装，类似用set方法构建对象，用这种必须在VO对象上加上注解@Builder
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        //把后端的结果再进行封装，将VO也封装到resul，交给前端使用
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping//添加请求方式和请求路径，因为类上已经添加路径这里就不考虑
    @ApiOperation("新增员工")// 接口文档的注释
    public Result save(@RequestBody EmployeeDTO employeeDTO) {//返回json数据，别忘了加RequestBody
        log.info("新增员工：{}",employeeDTO);
//        System.out.println("当前线程的id:"+Thread.currentThread().getId());
        employeeService.save(employeeDTO);
        return Result.success();//调用result封装返回成功
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")//在类的路径上请求page路径就可以
    @ApiOperation("员工分页查询") //接口文档注释
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){//接受前端传来的数据，用Query操作的DTO对象进行封装
        log.info("员工分页查询，参数为：{}",employeePageQueryDTO) ;//方便调试，输出提示信息

        //调用service实现查询功能
        PageResult pageResult=employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);//将响应结果封装到pageResult中返回
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用员工账号")
    @PostMapping("/status/{status}")//表示从下面的@Path中取得路径参数{status}
    public Result startOrStop(@PathVariable("status") Integer status , Long id){//注意路径参数传参要添加注解，地址栏传参只需要保证和地址栏变量同名
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id){//通过路径参数获取id，执行查询并利用Employee对象返回
        Employee employee =employeeService.getById(id);//service层
        return Result.success(employee);
    }

    /**
     *编辑员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("编辑员工信息")
    @PutMapping//路径不用写，就是类的源路径
    public Result update(@RequestBody EmployeeDTO employeeDTO){//和新增员工相似，传入的都是EmployeeDTO类型数据
        log.info("编辑员工信息:{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();//put操作,直接返回状态码就行无额外信息
    }
}
