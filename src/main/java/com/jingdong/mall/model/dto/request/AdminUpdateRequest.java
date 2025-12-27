package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新管理员信息请求参数
 */
@Data
public class AdminUpdateRequest {

    /**
     * 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255字符")
    private String avatar;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 用户名
     */
    @Size(min = 2, max = 50, message = "用户名长度2-50位")
    private String username;

    /**
     * 性别：男、女、未知
     */
    @Pattern(regexp = "^(男|女|未知)$", message = "性别只能是男、女或未知")
    private String gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 校验是否有可更新的字段
     */
    public boolean hasUpdatableFields() {
        return avatar != null || email != null || phone != null ||
                username != null || gender != null || birthday != null;
    }

    /**
     * 将字符串性别转换为数据库存储的整数
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