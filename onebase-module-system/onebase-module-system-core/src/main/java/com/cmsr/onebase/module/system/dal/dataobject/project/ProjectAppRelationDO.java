package com.cmsr.onebase.module.system.dal.dataobject.project;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目应用关联 DO
 *
 * @author system
 * @date 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "system_project_app_relation")
public class ProjectAppRelationDO extends BaseTenantEntity {

    // 字段列名常量
    public static final String PROJECT_ID = "project_id";
    public static final String APPLICATION_ID = "application_id";

    /**
     * 项目ID
     */
    @Column(value = PROJECT_ID)
    private Long projectId;

    /**
     * 应用ID
     */
    @Column(value = APPLICATION_ID)
    private Long applicationId;

}
