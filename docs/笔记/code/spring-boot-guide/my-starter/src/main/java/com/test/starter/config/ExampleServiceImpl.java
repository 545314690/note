package com.test.starter.config;

public class ExampleServiceImpl implements ExampleService {
    /**
     * ID
     */
    private String id;
    /**
     * ip
     */
    private String ip;

    /**
     * 构造函数
     *
     * @param id ID
     * @param ip IP
     */
    public ExampleServiceImpl(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    /**
     * 获取配置信息
     *
     * @return {@link ConfigureInfo}
     */
    @Override
    public ConfigureInfo configInfo() {
        return ConfigureInfo.builder()
                .id(this.id)
                .ip(this.ip).build();
    }
}