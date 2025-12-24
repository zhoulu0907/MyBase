package com.cmsr.onebase.module.dashboard.build.vo.template;

import lombok.Data;

/**
 * 仪表盘模板保存请求 VO
 */
@Data
public class DashboardTemplateSaveReqVO {

    /**
     * 模板ID（更新时使用）
     */
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板内容（JSON格式）
     */
    private String content;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 是否热门模板
     */
    private Integer hot;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 索引图片
     */
    private String indexImage;

}