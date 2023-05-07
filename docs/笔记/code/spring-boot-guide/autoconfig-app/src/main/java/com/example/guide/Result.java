package com.example.guide;

import lombok.Builder;
import lombok.Data;

/**
 * Result 通用返回工具类
 *
 */
@Data
@Builder
public class Result {
    /**
     * 成功CODE
     */
    public static final String SUCCESS_CODE = "0";
    /**
     * 成功MSG
     */
    public static final String SUCCESS_MSG = "SUCCESS!";
    /**
     * code
     */
    private String code;
    /**
     * msg
     */
    private String msg;
    /**
     * data
     */
    private Object data;
}