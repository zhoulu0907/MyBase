package com.cmsr.onebase.module.dashboard.build.model;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
@Table("dashboard_project_data")
@Data
public class GoviewProjectData extends BaseEntity {

    private Long projectId;

    private String createUserId;

    private String content;

    private Long appId;

}
