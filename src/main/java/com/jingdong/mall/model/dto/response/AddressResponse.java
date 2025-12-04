package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址响应DTO（已修改：补充字段顺序，对齐OpenAPI规范）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Integer id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private String postalCode; // 对应OpenAPI的postal_code字段
    private Boolean isDefault;
}