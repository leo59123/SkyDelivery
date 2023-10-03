package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类,用于创建AliOssUtil对象
 */
@Configuration //表明是一个配置类
@Slf4j
public class OssConfiguration {
    @Bean //告诉框架,项目启动时,创建这个对象并交给spring容器管理
    @ConditionalOnMissingBean //保证只有工具类对象一个,避免重复浪费
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){//从配置文件读得工具类的几个属性,以properties的形式注入
        log.info("开始创建阿里云文件上传工具类对象:{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
