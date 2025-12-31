package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.CouponMapper;
import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.request.CouponCreateRequest;
import com.jingdong.mall.model.dto.response.CouponListItemResponse;
import com.jingdong.mall.model.dto.response.CouponListResponse;
import com.jingdong.mall.model.dto.response.CouponCreateResponse;
import com.jingdong.mall.model.entity.Coupon;
import com.jingdong.mall.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取优惠券列表
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param request          请求参数
     * @return 优惠券列表响应结果
     */
    @Override
    public CouponListResponse getCouponList(Long currentUserId, Integer currentUserRole, CouponListRequest request) {
        // 仅管理员可用（role=1或2）
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        // 传入的请求对象已经由Controller层做了基本的校验（@Valid），Service层不再重复校验字段格式
        try {
            List<CouponListItemResponse> list = couponMapper.selectCouponList(request);
            Integer total = couponMapper.countCouponList(request);

            CouponListResponse response = new CouponListResponse();
            response.setList(list);
            response.setTotal(total == null ? 0 : total);
            response.setPage(request.getPage());
            response.setPageSize(request.getPageSize());

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            // 业务异常包装
            throw new BusinessException(ErrorCode.COUPON_LIST_GET_FAILED, "查询优惠券列表失败: " + ex.getMessage());
        }
    }

    /**
     * 创建优惠券
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param request          请求参数
     * @return 创建优惠券响应结果
     */
    @Override
    public CouponCreateResponse createCoupon(Long currentUserId, Integer currentUserRole, CouponCreateRequest request) {
        // 仅管理员可用（role=1或2）
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        // 传入的请求对象已经由Controller层做了基本的校验（@Valid）
        try {
            // DTO -> entity
            Coupon coupon = new Coupon();
            coupon.setName(request.getName());
            coupon.setType(request.getType());
            coupon.setValue(request.getValue());
            coupon.setMinSpend(request.getMinSpend());
            // 解析时间字符串为 LocalDateTime
            try {
                coupon.setStartTime(LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER));
                coupon.setEndTime(LocalDateTime.parse(request.getEndTime(), DATE_TIME_FORMATTER));
            } catch (Exception pe) {
                throw new BusinessException(ErrorCode.DATE_FORMAT_ERROR, "日期格式不正确，请使用yyyy-MM-dd HH:mm:ss");
            }
            coupon.setTotalCount(request.getTotalCount() == null ? 0 : request.getTotalCount());
            coupon.setUsedCount(0);
            coupon.setStatus(request.getStatus() == null ? 1 : request.getStatus());

            int rows = couponMapper.insertCoupon(coupon);
            if (rows <= 0) {
                throw new BusinessException(ErrorCode.COUPON_CREATE_FAILED, "插入优惠券记录失败");
            }

            CouponCreateResponse response = new CouponCreateResponse();
            response.setId(coupon.getId());
            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.COUPON_CREATE_FAILED, "创建优惠券失败: " + ex.getMessage());
        }
    }
}
