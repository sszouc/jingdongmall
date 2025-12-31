package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.request.CouponCreateRequest;
import com.jingdong.mall.model.dto.response.CouponListResponse;
import com.jingdong.mall.model.dto.response.CouponCreateResponse;

public interface CouponService {
    /**
     * 获取优惠券列表
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param request          请求参数
     * @return 优惠券列表响应结果
     */
    CouponListResponse getCouponList(Long currentUserId, Integer currentUserRole, CouponListRequest request);

    /**
     * 创建优惠券
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param request          请求参数
     * @return 创建优惠券响应结果
     */
    CouponCreateResponse createCoupon(Long currentUserId, Integer currentUserRole, CouponCreateRequest request);
}
