package com.cmsr.onebase.module.infra.dal.dataobject.security;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.mybatisflex.annotation.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 租户安全记录表
 *
 * @author matianyu
 * @date 2025-11-12
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "infra_security_record")
public class SecurityRecordDO extends BaseDO {

    public static final String TENANT_ID = "tenant_id";
    public static final String USER_ID = "user_id";
    public static final String RECORD_TYPE = "record_type";
    public static final String RECORD_VALUE = "record_value";

    /**
     * 租户ID
     */
    @Column(name = TENANT_ID)
    private Long tenantId;

    /**
     * 用户ID
     */
    @Column(name = USER_ID)
    private Long userId;

    /**
     * 记录类型（PASSWORD_HISTORY-密码历史）
     */
    @Column(name = RECORD_TYPE)
    private String recordType;

    /**
     * 记录值（加密后的密码）
     */
    @Column(name = RECORD_VALUE)
    private String recordValue;

}