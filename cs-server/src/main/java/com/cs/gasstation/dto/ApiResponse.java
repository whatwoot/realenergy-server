package com.cs.gasstation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GasStation统一API响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 状态码，0表示成功，非0表示错误
     */
    private String code;

    /**
     * 响应结果说明
     */
    private String msg;

    /**
     * 具体响应数据
     */
    private T data;
}
