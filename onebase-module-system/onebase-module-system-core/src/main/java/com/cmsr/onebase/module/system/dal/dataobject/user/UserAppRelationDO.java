package com.cmsr.onebase.module.system.dal.dataobject.user;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户应用关联 DO
 */
@Data
@Table(name = "system_user_app_relation")
public class UserAppRelationDO extends TenantBaseDO {

    public static final Long PARENT_ID_ROOT = 0L;
    public static final String APPLICATION_ID           = "application_id";
    public static final String TENANT_ID                = "tenant_id";
    public static final String STATUS                   = "status";
    public static final String USER_ID                  = "user_id";


    /**
     *  用户id
     */
    @Column
    private Long userId;

    /**
     * 应用id
     */
    @Column
    private Long applicationId;

    /**
     * 状态
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;


}