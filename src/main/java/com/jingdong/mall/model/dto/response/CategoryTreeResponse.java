package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类树响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    private List<CategoryItem> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private String id;
        private String name;
        private String subTitle;
        private String themeColor;
    }
}