package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.CouponMapper;
import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.request.CouponCreateRequest;
import com.jingdong.mall.model.dto.request.CouponUpdateRequest;
import com.jingdong.mall.model.dto.response.CouponListItemResponse;
import com.jingdong.mall.model.dto.response.CouponListResponse;
import com.jingdong.mall.model.dto.response.CouponCreateResponse;
import com.jingdong.mall.model.entity.Coupon;
import com.jingdong.mall.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
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
        // 管理员权限
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        // 参数校验（防止无效分页参数）
        if (request.getPage() == null || request.getPage() <= 0 || request.getPageSize() == null || request.getPageSize() <= 0) {
            throw new BusinessException(ErrorCode.COUPON_INVALID_PARAM, "分页参数不合法");
        }

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
        } catch (DataAccessException dae) {
            // 数据库相关错误
            throw new BusinessException(ErrorCode.COUPON_DB_ERROR, "查询优惠券列表数据库错误");
        } catch (Exception ex) {
            // 其他未知错误
            throw new BusinessException(ErrorCode.COUPON_LIST_GET_FAILED, "查询优惠券列表失败");
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
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        try {
            // 名称重复检查
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                int exist = couponMapper.countByName(request.getName().trim());
                if (exist > 0) {
                    throw new BusinessException(ErrorCode.COUPON_NAME_DUPLICATE);
                }
            }

            // DTO -> entity
            Coupon coupon = new Coupon();
            coupon.setName(request.getName());
            coupon.setType(request.getType());
            coupon.setValue(request.getValue());
            coupon.setMinSpend(request.getMinSpend());
            try {
                coupon.setStartTime(LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER));
                coupon.setEndTime(LocalDateTime.parse(request.getEndTime(), DATE_TIME_FORMATTER));
            } catch (Exception pe) {
                throw new BusinessException(ErrorCode.DATE_FORMAT_ERROR, "日期格式不正确，请使用yyyy-MM-dd HH:mm:ss");
            }
            coupon.setTotalCount(request.getTotalCount() == null ? 0 : request.getTotalCount());
            coupon.setUsedCount(0);
            coupon.setStatus(request.getStatus() == null ? 1 : request.getStatus());

            try {
                int rows = couponMapper.insertCoupon(coupon);
                if (rows <= 0) {
                    throw new BusinessException(ErrorCode.COUPON_CREATE_FAILED, "插入优惠券记录失败");
                }
            } catch (DuplicateKeyException dke) {
                throw new BusinessException(ErrorCode.COUPON_NAME_DUPLICATE);
            } catch (DataAccessException dae) {
                throw new BusinessException(ErrorCode.COUPON_DB_ERROR, "插入优惠券数据库错误");
            }

            CouponCreateResponse response = new CouponCreateResponse();
            response.setId(coupon.getId());
            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.COUPON_CREATE_FAILED, "创建优惠券失败");
        }
    }

    /**
     * 更新优惠券
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param id                优惠券ID
     * @param request          请求参数
     */
    @Override
    public void updateCoupon(Long currentUserId, Integer currentUserRole, Long id, CouponUpdateRequest request) {
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.COUPON_UPDATE_FAILED, "优惠券ID无效");
        }

        try {
            if (request.getStartTime() != null) {
                try {
                    LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER);
                } catch (Exception pe) {
                    throw new BusinessException(ErrorCode.DATE_FORMAT_ERROR, "日期格式不正确，请使用yyyy-MM-dd HH:mm:ss");
                }
            }
            if (request.getEndTime() != null) {
                try {
                    LocalDateTime.parse(request.getEndTime(), DATE_TIME_FORMATTER);
                } catch (Exception pe) {
                    throw new BusinessException(ErrorCode.DATE_FORMAT_ERROR, "日期格式不正确，请使用yyyy-MM-dd HH:mm:ss");
                }
            }

            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                int exist = couponMapper.countByNameExcludeId(request.getName().trim(), id);
                if (exist > 0) {
                    throw new BusinessException(ErrorCode.COUPON_NAME_DUPLICATE);
                }
            }

            try {
                int rows = couponMapper.updateCoupon(id, request);
                if (rows <= 0) {
                    throw new BusinessException(ErrorCode.COUPON_UPDATE_FAILED, "更新优惠券失败或优惠券不存在");
                }

            } catch (DuplicateKeyException dke) {
                throw new BusinessException(ErrorCode.COUPON_NAME_DUPLICATE);
            } catch (DataAccessException dae) {
                throw new BusinessException(ErrorCode.COUPON_DB_ERROR, "更新优惠券数据库错误");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.COUPON_UPDATE_FAILED, "更新优惠券失败");
        }
    }

    /**
     * 删除优惠券
     *
     * @param currentUserId    当前用户ID
     * @param currentUserRole  当前用户角色
     * @param id                优惠券ID
     */
    @Override
    public void deleteCoupon(Long currentUserId, Integer currentUserRole, Long id) {
        if (currentUserRole == null || (currentUserRole != 1 && currentUserRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.COUPON_DELETE_FAILED, "优惠券ID无效");
        }

        try {
            try {
                int rows = couponMapper.deleteCoupon(id);
                if (rows <= 0) {
                    throw new BusinessException(ErrorCode.COUPON_DELETE_FAILED, "删除优惠券失败或优惠券不存在");
                }
            } catch (DataAccessException dae) {
                throw new BusinessException(ErrorCode.COUPON_DB_ERROR, "删除优惠券数据库错误");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.COUPON_DELETE_FAILED, "删除优惠券失败");
        }
    }
}
