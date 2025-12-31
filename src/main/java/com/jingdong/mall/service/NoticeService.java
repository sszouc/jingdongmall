// service/NoticeService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.NoticeCreateRequest;
import com.jingdong.mall.model.dto.request.NoticeQueryRequest;
import com.jingdong.mall.model.dto.request.NoticeUpdateRequest;
import com.jingdong.mall.model.dto.response.NoticeDetailResponse;
import com.jingdong.mall.model.dto.response.NoticeListResponse;

public interface NoticeService {

    NoticeListResponse getNoticeList(Integer currentUserRole,NoticeQueryRequest request);

    NoticeDetailResponse getNoticeDetail(Long id);

    Long createNotice(Integer currentUserRole,NoticeCreateRequest request);

    void updateNotice(Integer currentUserRole,Long id, NoticeUpdateRequest request);

    void deleteNotice(Integer currentUserRole,Long id);

}