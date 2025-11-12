package com.cmsr.onebase.module.system.dal.dataobject.corpapprelation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 企业应用关联 DO
 */
@Data
@Entity
@Table(name = "system_corp_app_relation")
@EqualsAndHashCode(callSuper = true)
public class CorpAppRelationDO extends TenantBaseDO {
    public static final String APPLICATION_ID           = "application_id";
    public static final String CORP_ID                  = "corp_id";
    public static final String TENANT_ID                = "tenant_id";
    public static final String STATUS                   = "status";
    public static final String EXPIRES_TIME             = "expiresTime";

    /**
     * 应用id
     */
    @Column(name = "application_id", length = 10, nullable = false)
    private Long applicationId;

    /**
     * 企业id
     */
    @Column(name = "corp_id", length = 10, nullable = false)
    private Long corpId;

    @Column
    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

    @Column
    @Schema(description = "授权时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime authorizationTime ;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;


}