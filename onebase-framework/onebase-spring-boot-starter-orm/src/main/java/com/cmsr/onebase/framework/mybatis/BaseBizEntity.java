package com.cmsr.onebase.framework.mybatis;

import com.cmsr.onebase.framework.data.base.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseBizEntity extends BaseDO {

    private Long applicationId;

    private String loginEnv;

    private Long tenantId;

}
