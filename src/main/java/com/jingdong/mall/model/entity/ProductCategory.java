package com.jingdong.mall.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * 商品分类实体（对应数据库表product_category）
 */
@Data // lombok自动生成getter/setter
public class ProductCategory {
    // 对应数据库表的主键id
    private Integer id;
    // 分类名称
    private String name;
    // 分类详细信息（对应subTitle）
    private String subTitle;
    // 主题颜色
    private String themeColor;
    // 父分类id（0=一级分类）
    private Integer parentId;
    // 分类层级（1=一级，2=二级等）
    private Integer level;
    // 排序序号
    private Integer sortOrder;
    // 是否启用（1=启用，0=禁用）
    private Integer isActive;
    // 创建时间
    private Date createdTime;
    // 更新时间
    private Date updatedTime;
}