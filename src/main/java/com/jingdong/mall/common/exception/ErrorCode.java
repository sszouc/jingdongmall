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
    NOT_PERMISSION(1000,"您无权使用该接口"),
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
    INFO_UPDATE_FAIL(1012,"用户信息更新失败"),
    NEW_OLD_UNEQUAL(1013,"新密码不能与旧密码相同"),
    PASSWORD_CHANGE_FAIL(1014,"密码修改失败"),
    AVATAR_EMPTY(1015,"头像为空"),
    AVATAR_UPDATE_FAIL(1016,"头像上传失败"),
    INFO_GET_FAIL(1017,"获取用户信息失败"),
    LOGOUT_FAILED(1018,"退出登录失败"),
    DATE_FORMAT_ERROR(1019, "日期格式不正确，请使用yyyy-MM-dd格式"),

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
    BUY_NOW_QUANTITY_INVALID(2011, "购买数量不合法"),
    BUY_NOW_SPEC_INVALID(2012, "商品规格不合法"),
    SKU_INVALID(2013,"SKU不合法"),
    SKU_NOT_IN_CART(2014,"SKU不在购物车内"),

    // 订单相关 (3000-3999)
    ORDER_NOT_EXIST(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    ORDER_CREATE_FAILED(3003, "创建订单失败"),
    ORDER_ITEM_EMPTY(3004, "订单商品不能为空"),
    ORDER_ADDRESS_INVALID(3005, "收货地址无效"),
    ORDER_PAY_EXPIRED(3006, "订单支付已过期"),
    ORDER_CANCEL_FAILED(3007, "取消订单失败"),
    ORDER_ACCESS_DENIED(3008, "无权访问该订单"),
    ORDER_CANNOT_DELETE(3009, "订单不允许删除"),
    ORDER_DELETE_FAILED(3010, "订单删除失败"),
    ORDER_UPDATE_FAILED(3011, "订单更新失败"),
    ORDER_CANNOT_CONFIRM_RECEIPT(3012, "订单不能确认收货"),
    ORDER_CANNOT_CANCEL(3013, "订单不能取消"),
    ORDER_CANNOT_REFUND(3014, "订单不能申请退款"),
    ORDER_CANNOT_PAY(3015, "订单不能支付"),
    ORDER_LIST_GET_FAILED(3016,"订单列表获取失败"),
    ORDER_NOT_USER(3017,"该订单不属于你"),


    // 新增地址相关错误码（地址管理：4000-4999）
    ADDRESS_NOT_EXIST(4001, "地址不存在"),
    ADDRESS_ADD_FAILED(4002, "新增地址失败"),
    ADDRESS_UPDATE_FAILED(4003, "修改地址失败"),
    ADDRESS_DELETE_FAILED(4004, "删除地址失败"),
    ADDRESS_ID_NOT_NULL(4005,"地址ID不能为空"),

    // 购物车相关 (5000-5999)
    CART_ITEM_NOT_EXIST(5001, "购物车项不存在"),
    CART_ITEM_STOCK_NOT_ENOUGH(5002, "购物车商品库存不足"),

    // 图片相关(6000-6999)
    PHOTO_NOT_EMPTY(6000,"图片不能为空"),
    PHOTO_BIG(6001,"图片太大"),
    PHOTO_CONTENT_TYPE(6002,"只能上传图片文件"),
    PHOTO_CONTENT(6003,"不支持的文件格式，请上传jpg、png、gif、bmp等图片格式"),
    PHOTO_UPDATE_FAILED(6004,"图片上传失败"),

    // Token相关(7000-7999)
    TOKEN_EXPIRED(7000,"Token已过期，请尝试重新登录"),
    TOKEN_INVALID_FORMAT(7001,"Token格式错误，请尝试重新登录"),
    TOKEN_INVALID_SIGNATURE(7002,"Token签名无效，请尝试重新登录"),
    TOKEN_ERROR(7003,"Token验证失败，请尝试重新登录"),
    TOKEN_ADD_BLACKLIST_FAILED(7004,"Token加入黑名单失败"),

    // 新增管理员相关错误码（8000-8999）
    ADMIN_NOT_PERMISSION(8000, "需要管理员权限"),
    ADMIN_CANNOT_DELETE_SELF(8001, "不能删除自己"),
    ADMIN_CANNOT_DELETE_ADMIN(8002, "不能删除管理员用户"),
    ADMIN_CANNOT_DELETE_SUPER_ADMIN(8003, "不能删除超级管理员"),
    ADMIN_OPERATION_FAILED(8004, "管理员操作失败"),
    ADMIN_QUERY_FAILED(8005, "查询用户列表失败"),
    USER_ALREADY_BANNED(8006, "用户已被封禁"),
    USER_NOT_BANNED(8007, "用户未被封禁"),
    BAN_TIME_INVALID(8008, "封禁时长不合法"),
    ADMIN_CREATE_FAILED(8009, "创建管理员失败"),
    ADMIN_PHONE_ALREADY_EXISTS(8010, "该手机号已经是管理员"),
    ADMIN_GENERATE_USERNAME_FAILED(8011, "生成用户名失败"),
    ADMIN_DELETE_FAILED(8012, "删除管理员失败"), // 新增
    LAST_ADMIN_CANNOT_BE_DELETED(8013, "至少需要保留一个管理员")
    ;


    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}