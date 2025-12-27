package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员列表响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminListResponse {

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 管理员列表
     */
    private List<AdminItem> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminItem {
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