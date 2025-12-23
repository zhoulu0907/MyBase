package com.cmsr.onebase.module.dashboard.build.vo.template;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仪表盘模板分页请求 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardTemplatePageReqVO extends PageParam {

    /**
     * 模板名称
     */
    private String templateName;

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
}