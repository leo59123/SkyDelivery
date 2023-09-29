package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
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
    public Result<String> logout() {
        return Result.success();
    }

}
