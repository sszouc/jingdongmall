// common/response/Result.java
package com.jingdong.mall.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
*返回体的模板
* */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer status;      // 状态码
    private String message;     // 提示信息
    private T data;             // 响应数据

    // 成功响应 - 无数据
    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    // 成功响应 - 有数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    // 成功响应 - 自定义消息
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 错误响应
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 错误响应 - 使用默认错误码
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // 参数错误
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    // 未授权
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    // 禁止访问
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    // 资源不存在
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
}