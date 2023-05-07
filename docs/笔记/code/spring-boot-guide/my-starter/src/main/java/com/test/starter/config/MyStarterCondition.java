package com.test.starter.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MyStarterCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata){
        String encoding = context.getEnvironment().getProperty("my.starter.enable");

        if("true".equals(encoding)){
            return  true;
        }
        return  false;
     }
}