package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面,实现公共字段自动填充的处理逻辑
 */
@Aspect//标注这是个切面
@Component//标注bean,交给Spring来管理
@Slf4j
/*方便记录日志,为了能够少写两行代码，不用每次都在类的最前边写上：private static final Logger logger = LoggerFactory.getLogger(this.XXX.class)
我们只需要在类前面添加注解@Slf4j，即可使用log日志的功能了
*/
public class AutoFillAspect {
    //一个典型的切面:通知+切入点(对那些类的哪些方法进行拦截)
    /**
     * 切入点
     */
    //表达式意为:返回值所有的*,拦截的是com.sky.mapper包下面所有的类和所有的方法,(..)匹配所有的参数类型
    //显然execution表达式拦截了所有的方法,我们只需要update 和 insert
    //所以还要满足拦截的方法加入了自定义注解
    @Pointcut("execution(* com.sky.mapper.*.*(..)) &&@annotation(com.sky.annotation.AutoFill)")//切入点表达式,对那些类的哪些方法拦截
    public void autoFillPointCut(){}

    /**
     * 前置通知,在通知中为公共字段赋值
     */
    @Before("autoFillPointCut()")//指定上面这个切入点匹配上表达式时,执行前置通知(因为在方法执行前就要自动填充)
    public void autoFill(JoinPoint joinPoint){//传入连接点,可以知道哪个方法被拦截到,以及被拦截方法的参数什么样
        log.info("开始进行公共字段的自动填充");

        //在这里为公共字段赋值
        //1:获取到当前被拦截方法的数据库操作类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType=autoFill.value();//获得数据库操作类型

        //2:获取到当前被拦截方法的实体对象
        Object[] args = joinPoint.getArgs();
        if(args==null || args.length==0) return ;
        Object entity = args[0];//注意这里实体不要写具体的因为后面还有别的类型

        //3:准备赋值的数据,时间,登录ID (BaseContext中 -> ThreadLocal)
        LocalDateTime now=LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4:根据当前不同的操作类型,为对应的属性通过反射来赋值
        if(operationType==OperationType.INSERT){
            //插入操作,为4个公共字段赋值
            try {
                //反射获得方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射,将准备好的数据 给对象赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType== OperationType.UPDATE){
            //更新操作,为2个公共字段赋值
            try{
//                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
//                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    //当拦截器拦截时会执行这个前置通知

}
