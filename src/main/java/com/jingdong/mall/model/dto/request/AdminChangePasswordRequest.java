package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改管理员密码请求参数
 */
@Data
public class AdminChangePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    @Size(min = 6, max = 20, message = "原密码长度6-20位")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度6-20位")
    private String newPassword;
}