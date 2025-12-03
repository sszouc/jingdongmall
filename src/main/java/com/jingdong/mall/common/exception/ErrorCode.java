// common/exception/ErrorCode.java
package com.jingdong.mall.common.exception;

/*
 * 错误码的常量定义，在新建API的时候，都要注意要处理哪些业务错误，根据功能新增错误码
 * 系统错误后端就不捕获了，我们只捕获业务错误
 * */

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    // 业务错误码 (1000-1999)
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_EXISTED(1002, "用户已存在"),
    PHONE_EXISTED(1003, "手机号已注册"),
    EMAIL_EXISTED(1004, "邮箱已注册"),
    PASSWORD_ERROR(1005, "密码错误"),
    VERIFY_CODE_ERROR(1006, "验证码错误"),
    VERIFY_CODE_EXPIRED(1007, "验证码已过期"),
    AGREEMENT_NOT_ACCEPTED(1008, "请同意用户协议"),
    PHONE_FORMAT_ERROR(1009,"手机号格式不正确"),
    ACCOUNT_OR_PASSWORD_ERROR(1010, "账号或密码错误"),
    USER_DISABLED(1011, "用户已被禁用"),
    TOKEN_EXPIRED(1012,"Token已过期"),
    TOKEN_INVALID_FORMAT(1013,"Token格式错误"),
    TOKEN_INVALID_SIGNATURE(1014,"Token签名无效"),
    TOKEN_ERROR(1015,"Token验证失败"),

    // 商品相关 (2000-2999)
    PRODUCT_NOT_EXIST(2001, "商品不存在"),
    PRODUCT_STOCK_NOT_ENOUGH(2002, "商品库存不足"),

    // 订单相关 (3000-3999)
    ORDER_NOT_EXIST(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}