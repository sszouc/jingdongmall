package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AddressAddRequest;
import com.jingdong.mall.model.dto.request.AddressUpdateRequest;
import com.jingdong.mall.model.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    // 原有方法
    List<AddressResponse> getUserAddresses(Long userId);

    // 新增方法：新增地址
    AddressResponse addAddress(Long userId, AddressAddRequest request);

    // 新增方法：修改地址
    AddressResponse updateAddress(Long userId, AddressUpdateRequest request);

    // 新增方法：删除地址
    boolean deleteAddress(Long userId, Integer addressId);
}