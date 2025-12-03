// model/dto/request/ResetPasswordRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 找回密码请求参数
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String code;

    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String newPassword;
}