package com.cmsr.onebase.module.system.dal.dataobject.corpapprelation;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 企业应用关联 DO
 */
@Data
@Table(value = "system_corp_app_relation")
@EqualsAndHashCode(callSuper = true)
public class CorpAppRelationDO extends BaseTenantEntity {
    public static final String APPLICATION_ID           = "application_id";
    public static final String CORP_ID                  = "corp_id";
    public static final String TENANT_ID                = "tenant_id";
    public static final String STATUS                   = "status";
    public static final String EXPIRES_TIME             = "expires_time";

    /**
     * 应用id
     */
    @Column(value = "application_id")
    private Long applicationId;

    /**
     * 企业id
     */
    @Column(value = "corp_id")
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