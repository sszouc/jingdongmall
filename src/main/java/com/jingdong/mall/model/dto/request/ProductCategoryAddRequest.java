package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增分类请求参数
 */
@Data
public class ProductCategoryAddRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100字符")
    private String name;

    @Size(max = 200, message = "分类详细信息长度不能超过200字符")
    private String subTitle; // 允许为空

    @Size(max = 255, message = "主题颜色长度不能超过255字符")
    private String themeColor; // 允许为空
}