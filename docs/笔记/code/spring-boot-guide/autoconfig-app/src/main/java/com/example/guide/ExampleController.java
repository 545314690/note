package com.example.guide;

import com.test.starter.config.ConfigureInfo;
import com.test.starter.config.ExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.guide.Result.SUCCESS_CODE;
import static com.example.guide.Result.SUCCESS_MSG;


/**
 * 示例项目测试控制器
 *
 */
@RestController
@RequestMapping(value = "/example")
public class ExampleController {

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    /**
     * 获取配置的IP 和ID
     *
     * @return {@link Result}
     */
    @GetMapping(value = "config")
    public Result configInfo() {
        // 获取配置信息
        ConfigureInfo configureInfo = exampleService.configInfo();
        // 封装返回
        return Result.builder()
                .code(SUCCESS_CODE)
                .msg(SUCCESS_MSG)
                .data(configureInfo).build();
    }

    /**
     * 注入 ExampleService
     */
    private final ExampleService exampleService;
}