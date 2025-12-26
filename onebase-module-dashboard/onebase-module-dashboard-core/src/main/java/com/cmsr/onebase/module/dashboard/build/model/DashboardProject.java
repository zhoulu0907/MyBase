package com.cmsr.onebase.module.dashboard.build.model;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
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
@Table("dashboard_project")
@Data
public class DashboardProject extends BaseTenantEntity {

    private String projectName;

    private Integer state;

    private String indexImage;

    private String remarks;

    private Long appId;

}