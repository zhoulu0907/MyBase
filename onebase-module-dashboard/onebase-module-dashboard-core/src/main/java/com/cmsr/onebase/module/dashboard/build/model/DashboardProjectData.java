package com.cmsr.onebase.module.dashboard.build.model;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
@Table("dashboard_project_data")
@Data
public class DashboardProjectData extends BaseEntity {

    private Long projectId;

    private String content;

    private Long appId;

}
