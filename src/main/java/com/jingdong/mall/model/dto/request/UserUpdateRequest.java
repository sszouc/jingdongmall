// src/main/java/com/jingdong/mall/model/dto/request/UserUpdateRequest.java
package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新用户信息请求参数
 */
@Data
public class UserUpdateRequest {

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 生日
     */
    @PastOrPresent(message = "生日不能是未来日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 性别：男、女、未知
     */
    @Pattern(regexp = "^(男|女|未知)$", message = "性别只能是男、女或未知")
    private String gender;

//    /**
//     * 验证非空：至少更新一个字段
//     */
//    public boolean isAnyFieldPresent() {
//        return avatar != null || birthday != null || gender != null || email != null || phone != null || ;
//    }

    /**
     * 将字符串性别转换为数据库存储的整数
     * @return 性别对应的整数值
     */
    public Integer getGenderAsInt() {
        if (gender == null) {
            return null;
        }

        return switch (gender) {
            case "男" -> 1;
            case "女" -> 2;
            case "未知" -> 0;
            default -> 0; // 默认为保密/未知
        };
    }
}