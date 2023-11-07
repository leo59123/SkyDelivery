package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
@Target(ElementType.METHOD)//解释当前注解会加在什么位置，此时指定只能加在方法上
@Retention(RetentionPolicy.RUNTIME)//解释生命周期
public @interface AutoFill {
    //指定数据库操作类型:UPDATE INSERT,通过了枚举的方式来指定,枚举类定义为OperationType
    OperationType value();
}
