// service/impl/AddressServiceImpl.java
package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.AddressMapper;
import com.jingdong.mall.model.entity.Address;
import com.jingdong.mall.model.dto.response.AddressResponse;
import com.jingdong.mall.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 查询用户地址列表
        List<Address> addresses = addressMapper.selectByUserId(userId);

        // 3. 转换为响应DTO
        return convertToResponseList(addresses);
    }

    private List<AddressResponse> convertToResponseList(List<Address> addresses) {
        return addresses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AddressResponse convertToResponse(Address address) {
        if (address == null) {
            return null;
        }

        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setName(address.getName());
        response.setPhone(address.getPhone());
        response.setPostalCode(address.getPostalCode());
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetail(address.getDetail());
        response.setIsDefault(address.getIsDefault());

        return response;
    }
}