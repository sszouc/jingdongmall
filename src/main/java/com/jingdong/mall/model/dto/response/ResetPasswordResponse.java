// model/dto/response/ResetPasswordResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 找回密码响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponse {
    private Integer status;      // 状态码
    private String message;     // 提示信息
}