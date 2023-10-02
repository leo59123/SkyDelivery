package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);//自定义异常类，代表账号不存在，放置在common中
            // 然而抛出异常是需要进行捕获的，在./hander 中那个全局异常捕获中统一处理，用父类和继承来处理
        }

        //密码比对
        // 对前端输入传递过来的明纹密码进行加密，以便和数据库中加密后的密码匹配
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);//输出常量类 message类的提示信息
        }
        //状态是否被锁定 ，0代表禁用，1代表启用
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO){
//        System.out.println("当前线程的id:"+Thread.currentThread().getId());
         Employee employee= new Employee();//创建新用户对象

        //将DTO传送进来的数据赋值，要么用set，要么用spring的对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);//属性名一致的对象就可以拷贝 name ->name 这样的

        //此时只拷贝了DTO中相对应的那几个状态，employ中还有几个状态需要设置
        employee.setStatus(StatusConstant.ENABLE);//规范情况下，使用常量类来方便维护

        //设置默认密码,注意MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));//同样使用常量类来方便维护

        //设置创建时间，修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());


        //设置创建人，修改人ID  ,从ThreadLocal封装对象中获取拦截器动态获取的 用户id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //用Mapper 将新数据持久化到数据库
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //底层实际上是 实现select & from employee limit 0,10这样子  ，而DTO中应该给了我们需要的参数
        // 正常做的话，我们要利用参数计算每次查询的页和位置，然后执行对应select 操作
        // PageHelper 插件：用框架给出的工具帮我们实现一定的功能，只要传入页码和页数
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());//怎么做到的呢？底层用ThreadLocal存储，计算分页，然后生成动态sql

        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);//到mapper层对数据库进行操作，分页查询

        //从插件查到的 page对象中获取 响应信息的对象
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total,records );
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status,Long id){
        //调用mapper实现：update employee set status =? where id =?  ,这两个参数动态添加

//        Employee employee=new Employee();
//        employee.setStatus(status);
//        employee.setId(id);
        //设置两个参数，要么调用set方法，要么使用@Builder 注解构建器构建
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        //使用更通用的更新方法：传入employee对象
        employeeMapper.update(employee);
    }
}
