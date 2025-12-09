package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.OrderItem;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * 订单项动态SQL提供者
 */
public class OrderItemSqlProvider {

    /**
     * 批量插入订单项
     */
    public String batchInsert(List<OrderItem> orderItems) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO order_item (order_id, sku_id, product_name, sku_specs, main_image, price, " +
                "quantity, total_price, after_sale_status, created_time, updated_time) VALUES ");

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            sql.append("(")
                    .append("#{orderItems[").append(i).append("].orderId}, ")
                    .append("#{orderItems[").append(i).append("].skuId}, ")
                    .append("#{orderItems[").append(i).append("].productName}, ")
                    .append("#{orderItems[").append(i).append("].skuSpecs}, ")
                    .append("#{orderItems[").append(i).append("].mainImage}, ")
                    .append("#{orderItems[").append(i).append("].price}, ")
                    .append("#{orderItems[").append(i).append("].quantity}, ")
                    .append("#{orderItems[").append(i).append("].totalPrice}, ")
                    .append("0, ") // after_sale_status 默认为0（无售后）
                    .append("NOW(), NOW())");

            if (i < orderItems.size() - 1) {
                sql.append(", ");
            }
        }

        return sql.toString();
    }
}