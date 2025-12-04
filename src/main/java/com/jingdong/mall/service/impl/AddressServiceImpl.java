package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.AddressMapper;
import com.jingdong.mall.model.dto.request.AddressAddRequest;
import com.jingdong.mall.model.dto.request.AddressUpdateRequest;
import com.jingdong.mall.model.dto.response.AddressResponse;
import com.jingdong.mall.model.entity.Address;
import com.jingdong.mall.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    // 原有方法保持不变
    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        List<Address> addresses = addressMapper.selectByUserId(userId);
        return convertToResponseList(addresses);
    }

    // 新增方法：新增地址
    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressAddRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 如果设置为默认地址，先取消用户现有默认地址
        Address address = new Address();
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            int defaultCount = addressMapper.countDefaultByUserId(userId);
            if (defaultCount > 0) {
                addressMapper.cancelAllDefault(userId);
            }
        }

        // 3. 保存地址
        int result = addressMapper.insert(address);
        if (result <= 0) {
            throw new BusinessException("新增地址失败，请稍后重试");
        }

        // 4. 返回新增地址信息
        Address savedAddress = addressMapper.selectByUserId(userId).stream()
                .filter(a -> a.getId().equals(address.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("获取新增地址信息失败"));
        return convertToResponse(savedAddress);
    }

    // 新增方法：修改地址
    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, AddressUpdateRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (request.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }

        // 2. 检查地址是否存在
        List<Address> addresses = addressMapper.selectByUserId(userId);
        boolean addressExists = addresses.stream()
                .anyMatch(a -> a.getId().equals(request.getId()));
        if (!addressExists) {
            throw new BusinessException("地址不存在");
        }

        // 3. 如果设置为默认地址，先取消用户现有默认地址
        Address address = new Address();
        address.setId(request.getId());
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            int defaultCount = addressMapper.countDefaultByUserId(userId);
            if (defaultCount > 0) {
                addressMapper.cancelAllDefault(userId);
            }
        }

        // 4. 更新地址
        int result = addressMapper.update(address);
        if (result <= 0) {
            throw new BusinessException("修改地址失败，请稍后重试");
        }

        // 5. 返回更新后地址信息
        Address updatedAddress = addressMapper.selectByUserId(userId).stream()
                .filter(a -> a.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("获取更新地址信息失败"));
        return convertToResponse(updatedAddress);
    }

    // 新增方法：删除地址
    @Override
    @Transactional
    public boolean deleteAddress(Long userId, Integer addressId) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (addressId == null) {
            throw new BusinessException("地址ID不能为空");
        }

        // 2. 检查地址是否存在
        List<Address> addresses = addressMapper.selectByUserId(userId);
        boolean addressExists = addresses.stream()
                .anyMatch(a -> a.getId().equals(addressId));
        if (!addressExists) {
            throw new BusinessException("地址不存在");
        }

        // 3. 执行删除
        int result = addressMapper.deleteById(addressId, userId);
        if (result <= 0) {
            throw new BusinessException("删除地址失败，请稍后重试");
        }
        return true;
    }

    // 原有工具方法保持不变
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
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetail(address.getDetail());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault());
        return response;
    }
}