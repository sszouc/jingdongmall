// src/main/java/com/jingdong/mall/model/dto/request/UserUpdateRequest.java
package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 更新用户信息请求参数
 */
@Data
public class UserUpdateRequest {

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500字符")
    private String avatar;

    /**
     * 生日
     */
    @Past(message = "生日不能是未来日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 性别：男、女、未知
     */
    @Pattern(regexp = "^(男|女|未知)$", message = "性别只能是男、女或未知")
    private String gender;

    /**
     * 验证非空：至少更新一个字段
     */
    public boolean isAnyFieldPresent() {
        return avatar != null || birthday != null || gender != null;
    }

    /**
     * 将字符串性别转换为数据库存储的整数
     * @return 性别对应的整数值
     */
    public Integer getGenderAsInt() {
        if (gender == null) {
            return null;
        }

        switch (gender) {
            case "男":
                return 1;
            case "女":
                return 2;
            case "未知":
                return 0;
            default:
                return 0; // 默认为保密/未知
        }
    }
}