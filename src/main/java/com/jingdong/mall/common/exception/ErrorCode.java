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
    PRODUCT_STOCK_NOT_ENOUGH(2002, "商品暂无库存"),
    PRODUCT_DETAIL_ERROR(2003, "获取商品详情失败"),
    PRODUCT_ID_NULL(2004,"商品ID不能为空"),
    MIN_PRICE_ZERO(2005,"最低价格不能小于0"),
    MAX_PRICE_ZERO(2006,"最高价格不能小于0"),
    MAX_LOWER_MIN(2007,"最低价格不能大于最高价格"),
    CATEGORY_ID_INVALID(2008,"分类ID不合法"),
    SORT_INVALID(2009,"排序方式不合法"),
    SKU_NOT_EXIST(2010,"SKU不存在"),

    // 订单相关 (3000-3999)
    ORDER_NOT_EXIST(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    ORDER_CREATE_FAILED(3003, "创建订单失败"),
    ORDER_ITEM_EMPTY(3004, "订单商品不能为空"),
    ORDER_ADDRESS_INVALID(3005, "收货地址无效"),
    ORDER_PAY_EXPIRED(3006, "订单支付已过期"),
    ORDER_CANCEL_FAILED(3007, "取消订单失败"),


    // 新增地址相关错误码（地址管理：4000-4999）
    ADDRESS_NOT_EXIST(4001, "地址不存在"),
    ADDRESS_ADD_FAILED(4002, "新增地址失败"),
    ADDRESS_UPDATE_FAILED(4003, "修改地址失败"),
    ADDRESS_DELETE_FAILED(4004, "删除地址失败"),

    // 购物车相关 (5000-5999)
    CART_ITEM_NOT_EXIST(5001, "购物车项不存在"),
    CART_ITEM_STOCK_NOT_ENOUGH(5002, "购物车商品库存不足");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}