package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员用户列表响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 用户列表
     */
    private List<UserItem> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserItem {
        private Long id;
        private String username;
        private String phone;
        private String email;
        private String avatar;
        private String status; // "active" 或 "disabled"
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedTime;
    }
}