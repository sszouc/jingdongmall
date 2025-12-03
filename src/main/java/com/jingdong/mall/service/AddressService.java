// service/AddressService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getUserAddresses(Long userId);
}