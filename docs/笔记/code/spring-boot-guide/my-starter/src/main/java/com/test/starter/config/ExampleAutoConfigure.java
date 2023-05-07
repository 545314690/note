package com.test.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 示例自动配置类
 *
 */
@Configuration
//@Conditional(MyStarterCondition.class)
@ConditionalOnProperty(prefix = "my.service",value = "enabled",havingValue = "true")
@EnableConfigurationProperties(value = ExampleProperties.class)
public class ExampleAutoConfigure {

    private Logger logger = LoggerFactory.getLogger(ExampleAutoConfigure.class);

    /**
     * 配置ExampleService
     *
     * @return {@link ExampleService}
     */
    @Bean
    @ConditionalOnMissingBean
    public ExampleService exampleService() {
        logger.info("Config ExampleService Start...");
        ExampleServiceImpl service = new ExampleServiceImpl(properties.getId(), properties.getIp());
        logger.info("Config ExampleService End.");
        return service;
    }

    /**
     * 注入ExampleProperties
     */
    private final ExampleProperties properties;

    public ExampleAutoConfigure(ExampleProperties properties) {
        this.properties = properties;
    }
}