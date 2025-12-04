package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增地址请求参数
 */
@Data
public class AddressAddRequest {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份长度不能超过50字符")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市长度不能超过50字符")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Size(max = 50, message = "区县长度不能超过50字符")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址长度不能超过255字符")
    private String detail;

    @Size(max = 10, message = "邮政编码长度不能超过10字符")
    private String postalCode;

    private Boolean isDefault = false; // 默认非默认地址
}