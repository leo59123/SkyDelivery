package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {//Redis配置类
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){//编写配置类，来创建RedisTemplate对象
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate<>();
        //设置redis的连接工厂对象，yml中引用的starter会自动创建好然后声明就可以传入这个函数作为参数
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器-String类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;//返回创建好的模板对象
    }
}
