package com.cmsr.onebase.module.system.dal.dataobject.project;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目信息 DO
 *
 * @author claude
 * @date 2026-03-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "project_info")
public class ProjectInfoDO extends BaseTenantEntity {

    // 字段列名常量
    public static final String PROJECT_CODE = "project_code";
    public static final String PROJECT_NAME = "project_name";
    public static final String SOURCE_PLATFORM = "source_platform";
    public static final String EXTERNAL_PROJECT_ID = "external_project_id";
    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";
    public static final String INDUSTRY_TAG = "industry_tag";
    public static final String PROJECT_COVER = "project_cover";

    /**
     * 项目编码（租户内唯一，来自URL参数code）
     */
    @Column(value = PROJECT_CODE)
    private String projectCode;

    /**
     * 项目名称
     */
    @Column(value = PROJECT_NAME)
    private String projectName;

    /**
     * 来源平台
     * 枚举 {@link com.cmsr.onebase.module.system.enums.project.ProjectSourceEnum}
     */
    @Column(value = SOURCE_PLATFORM)
    private String sourcePlatform;

    /**
     * 外部平台项目ID（预留扩展）
     */
    @Column(value = EXTERNAL_PROJECT_ID)
    private String externalProjectId;

    /**
     * 状态
     * 枚举 {@link com.cmsr.onebase.framework.common.enums.CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;

    /**
     * 描述
     */
    @Column(value = DESCRIPTION)
    private String description;

    /**
     * 行业标签
     */
    @Column(value = INDUSTRY_TAG)
    private String industryTag;

    /**
     * 项目封面
     */
    @Column(value = PROJECT_COVER)
    private String projectCover;


}