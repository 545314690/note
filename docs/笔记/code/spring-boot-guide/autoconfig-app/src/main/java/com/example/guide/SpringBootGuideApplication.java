package com.example.guide;

import com.example.autoconfig.Cat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@EnableAutoConfiguration
public class SpringBootGuideApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =SpringApplication.run(SpringBootGuideApplication.class, args);
        Cat cat = context.getBean(Cat.class);
        System.out.println(cat);
    }

}
