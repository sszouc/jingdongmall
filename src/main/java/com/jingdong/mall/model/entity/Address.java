// model/entity/Address.java
package com.jingdong.mall.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Address {
    private Integer id;
    private Integer userId;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private String postalCode;
    private Boolean isDefault;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}