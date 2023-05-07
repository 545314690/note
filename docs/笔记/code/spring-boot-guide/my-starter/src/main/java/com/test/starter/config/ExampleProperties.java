package com.test.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.test.starter.config.ExampleProperties.DEFAULT_PREFIX;


/**
 * 配置属性项
 *
 */
@Data
@ConfigurationProperties(value = DEFAULT_PREFIX)
public class ExampleProperties {
    /**
     * PREFIX
     */
    public static final String DEFAULT_PREFIX = "com.test.example";
    /**
     * ID标识
     */
    private String id;

    /**
     * IP地址
     */
    private String ip;

}