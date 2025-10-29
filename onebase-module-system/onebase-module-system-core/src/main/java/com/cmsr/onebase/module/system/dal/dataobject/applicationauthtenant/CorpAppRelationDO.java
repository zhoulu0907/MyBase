package com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CorpAppRelationDO extends BaseDO {

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

    /**
     * 空间id
     */
    @Column(name = "tenant_id", length = 10)
    private Integer tenantId;

    @Schema(description = "过期时间")
    @JsonProperty("expiresTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresTime;


    @Schema(description = "授权时间")
    @JsonProperty("authorizationTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime authorizationTime;




}