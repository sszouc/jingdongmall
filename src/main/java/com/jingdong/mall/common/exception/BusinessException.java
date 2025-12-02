// common/exception/BusinessException.java

/*
 * 业务异常的构造方法，捕获异常的时候都要调用这里的类
 *
 */
package com.jingdong.mall.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;    // 错误码
    private String message;  // 错误信息

    // 构造方法 - 只有错误信息，使用默认错误码
    public BusinessException(String message) {
        super(message);
        this.code = 500;    // 默认业务错误码
        this.message = message;
    }

    // 构造方法 - 指定错误码和错误信息
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // 构造方法 - 错误码枚举
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    // 构造方法 - 错误码枚举和详细描述
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage() + ": " + detail;
    }
}