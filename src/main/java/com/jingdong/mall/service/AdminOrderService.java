// service/OrderService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.dto.request.AdminOrderStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminOrderQueryResponse;


public interface AdminOrderService {

    AdminOrderQueryResponse getOrderList(Integer currentUserRole, AdminOrderQueryRequest request);

    void updateOrderStatus(Integer currentUserRole,String orderSn, AdminOrderStatusUpdateRequest request);

    String getStatusDesc(Integer status);
}