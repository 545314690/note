package com.test.starter.config;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 配置信息
 */
@Data
@Builder
public class ConfigureInfo implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * IP地址
     */
    private String ip;
}
