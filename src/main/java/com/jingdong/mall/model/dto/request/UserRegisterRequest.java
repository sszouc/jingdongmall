// model/dto/request/UserRegisterRequest.java
package com.jingdong.mall.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

/*
* 不解释
* */
@Data
public class UserRegisterRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    //@NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String code;

    @AssertTrue(message = "必须同意用户协议")
    private Boolean agreeProtocol;
}