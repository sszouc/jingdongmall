// service/impl/NoticeServiceImpl.java
package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.NoticeMapper;
import com.jingdong.mall.model.dto.request.NoticeCreateRequest;
import com.jingdong.mall.model.dto.request.NoticeQueryRequest;
import com.jingdong.mall.model.dto.request.NoticeUpdateRequest;
import com.jingdong.mall.model.dto.response.NoticeDetailResponse;
import com.jingdong.mall.model.dto.response.NoticeListResponse;
import com.jingdong.mall.model.dto.response.NoticeResponse;
import com.jingdong.mall.model.entity.Notice;
import com.jingdong.mall.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    // 公告类型常量
    private static final int TYPE_SYSTEM = 1;   // 系统公告
    private static final int TYPE_ACTIVITY = 2; // 活动公告
    private static final int TYPE_LOGISTICS = 3; // 物流公告
    private static final int TYPE_OTHER = 4;    // 其他公告

    @Override
    public NoticeListResponse getNoticeList(Integer currentUserRole,NoticeQueryRequest request) {

        validateAdminPermission(currentUserRole);
        // 1. 查询公告列表
        List<Notice> notices = noticeMapper.selectNoticeList(request);

        // 2. 查询公告总数
        Long total = noticeMapper.countNoticeList(request);

        // 3. 转换为响应DTO
        List<NoticeResponse> noticeResponses = notices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 4. 构建响应
        NoticeListResponse response = new NoticeListResponse();
        response.setList(noticeResponses);
        response.setTotal(total);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

        return response;
    }

    @Override
    public NoticeDetailResponse getNoticeDetail(Long id) {
        // 1. 查询公告
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ErrorCode.NOTICE_NOT_EXIST);
        }

        // 2. 转换为响应DTO
        return convertToDetailResponse(notice);
    }

    @Override
    public Long createNotice(Integer currentUserRole,NoticeCreateRequest request) {

        validateAdminPermission(currentUserRole);

        // 1. 构建公告实体
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setType(request.getType() != null ? request.getType() : TYPE_SYSTEM);

        // 处理isActive
        if (request.getIsActive() != null) {
            notice.setIsActive(request.getIsActive() == 1);
        } else {
            notice.setIsActive(true); // 默认启用
        }

        // 处理sortOrder
        notice.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        // 2. 插入数据库
        int result = noticeMapper.insert(notice);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.NOTICE_CREATE_FAILED);
        }

        log.info("创建公告成功: id={}, title={}", notice.getId(), notice.getTitle());
        return notice.getId();
    }

    @Override
    public void updateNotice(Integer currentUserRole,Long id, NoticeUpdateRequest request) {

        validateAdminPermission(currentUserRole);

        // 1. 检查公告是否存在
        Notice existNotice = noticeMapper.selectById(id);
        if (existNotice == null) {
            throw new BusinessException(ErrorCode.NOTICE_NOT_EXIST);
        }

        // 2. 构建更新实体
        Notice notice = new Notice();
        notice.setId(id);

        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notice.setContent(request.getContent());
        }
        if (request.getType() != null) {
            notice.setType(request.getType());
        }
        if (request.getIsActive() != null) {
            notice.setIsActive(request.getIsActive() == 1);
        }
        if (request.getSortOrder() != null) { // 新增字段
            notice.setSortOrder(request.getSortOrder());
        }

        // 3. 更新数据库
        int result = noticeMapper.update(notice);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.NOTICE_UPDATE_FAILED);
        }

        log.info("更新公告成功: id={}", id);
    }

    @Override
    public void deleteNotice(Integer currentUserRole,Long id) {

        validateAdminPermission(currentUserRole);

        // 1. 检查公告是否存在
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ErrorCode.NOTICE_NOT_EXIST);
        }

        // 2. 删除公告
        int result = noticeMapper.deleteById(id);
        if (result <= 0) {
                throw new BusinessException(ErrorCode.NOTICE_DELETE_FAILED);
        }

        log.info("删除公告成功: id={}, title={}", id, notice.getTitle());
    }


    /**
     * 转换公告实体为列表响应DTO
     */
    private NoticeResponse convertToResponse(Notice notice) {
        NoticeResponse response = new NoticeResponse();
        BeanUtils.copyProperties(notice, response);

        // 设置时间字段
        response.setCreateTime(notice.getCreatedTime());
        response.setUpdateTime(notice.getUpdatedTime());

        // 设置类型描述
        response.setTypeDesc(getTypeDesc(notice.getType()));

        // 设置状态描述
        if (notice.getIsActive() != null) {
            response.setStatusDesc(notice.getIsActive() ? "启用" : "禁用");
        } else {
            response.setStatusDesc("未知");
        }

        // 转换isActive为Integer
        response.setIsActive(notice.getIsActive() ? 1 : 0);

        return response;
    }

    /**
     * 转换公告实体为详情响应DTO
     */
    private NoticeDetailResponse convertToDetailResponse(Notice notice) {
        NoticeDetailResponse response = new NoticeDetailResponse();
        BeanUtils.copyProperties(notice, response);

        // 转换isActive为Integer
        response.setIsActive(notice.getIsActive() ? 1 : 0);

        // 设置时间字段
        response.setCreateTime(notice.getCreatedTime());
        response.setUpdateTime(notice.getUpdatedTime());

        return response;
    }

    /**
     * 获取公告类型描述
     */
    private String getTypeDesc(Integer type) {
        if (type == null) {
            return "未知类型";
        }

        switch (type) {
            case TYPE_SYSTEM:
                return "系统公告";
            case TYPE_ACTIVITY:
                return "活动公告";
            case TYPE_LOGISTICS:
                return "物流公告";
            case TYPE_OTHER:
                return "其他公告";
            default:
                return "未知类型";
        }
    }

    /**
     * 验证管理员权限
     */
    private void validateAdminPermission(Integer currentUserRole) {
        if (currentUserRole == null || (currentUserRole != 2 && currentUserRole != 1)) {
            log.warn("非管理员尝试操作，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }
}