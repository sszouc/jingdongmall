// model/dto/response/AddressResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Integer id;
    private String name;
    private String phone;
    private String postalCode;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Boolean isDefault;
}