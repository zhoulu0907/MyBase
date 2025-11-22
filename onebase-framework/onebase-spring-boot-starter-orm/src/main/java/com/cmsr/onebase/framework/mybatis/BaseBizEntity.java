package com.cmsr.onebase.framework.mybatis;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBizEntity extends BaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "login_env")
    private String loginEnv;

    @Column(name = "tenant_id")
    private Long tenantId;

}
