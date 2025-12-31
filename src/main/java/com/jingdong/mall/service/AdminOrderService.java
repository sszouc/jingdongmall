// service/OrderService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.dto.request.AdminOrderStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminOrderQueryResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;


public interface AdminOrderService {

    /**
     * 获取订单列表
     * @param currentUserRole 角色
     * @param request 请求
     */
    AdminOrderQueryResponse getOrderList(Integer currentUserRole, AdminOrderQueryRequest request);

    /**
     * 更新订单状态
     * @param currentUserRole 角色
     * @param request 请求
     */

    void updateOrderStatus(Integer currentUserRole,String orderSn, AdminOrderStatusUpdateRequest request);

    /**
     * 获取订单详情
     * @param currentUserRole 角色
     * @param orderSn 订单号
     * @return 订单详情
     */
    OrderDetailResponse getOrderDetail(Integer currentUserRole, String orderSn);


}