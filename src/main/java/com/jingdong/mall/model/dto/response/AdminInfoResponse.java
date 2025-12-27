package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理员信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfoResponse {

    /**
     * 管理员ID
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
     * 手机号
     */
    private String phone;

    /**
     * 角色：0-普通用户，1-普通管理员，2-超级管理员
     */
    private Integer role;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 从User实体创建AdminInfoResponse
     */
    public static AdminInfoResponse fromEntity(com.jingdong.mall.model.entity.User user) {
        if (user == null) {
            return null;
        }

        AdminInfoResponse response = new AdminInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setGender(convertGenderToString(user.getGender()));
        response.setBirthday(user.getBirthday());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setCreateTime(user.getCreatedTime());

        return response;
    }

    /**
     * 将数据库中的整数性别转换为字符串
     */
    private static String convertGenderToString(Integer genderInt) {
        if (genderInt == null) {
            return "未知";
        }

        return switch (genderInt) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 获取角色描述
     */
    public String getRoleDescription() {
        if (role == null) {
            return "未知";
        }

        return switch (role) {
            case 0 -> "普通用户";
            case 1 -> "普通管理员";
            case 2 -> "超级管理员";
            default -> "未知";
        };
    }
}