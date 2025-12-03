// src/main/java/com/jingdong/mall/model/dto/response/UserInfoResponse.java
package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 性别：男、女、未知
     */
    private String gender;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 从User实体创建响应
     */
    public static UserInfoResponse fromEntity(com.jingdong.mall.model.entity.User user) {
        if (user == null) {
            return null;
        }

        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setGender(convertGenderToString(user.getGender()));
        response.setBirthday(user.getBirthday());

        return response;
    }

    /**
     * 将数据库中的整数性别转换为字符串
     * @param genderInt 数据库中的性别整数值
     * @return 字符串形式的性别
     */
    private static String convertGenderToString(Integer genderInt) {
        if (genderInt == null) {
            return "未知";
        }

        switch (genderInt) {
            case 1:
                return "男";
            case 2:
                return "女";
            case 0:
            default:
                return "未知";
        }
    }
}