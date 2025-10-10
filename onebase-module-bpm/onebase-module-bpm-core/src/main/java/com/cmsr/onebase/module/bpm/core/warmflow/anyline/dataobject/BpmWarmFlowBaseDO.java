package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.dromara.warm.flow.core.entity.Definition;

import java.util.Date;

/**
 * Warm-Flow 流程定义基础对象
 *
 * 手动实现与BaseDO的差异的字段
 *
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BpmWarmFlowBaseDO<T> extends BaseDO {
    public static final String TENANT_ID = "tenant_id";

    /**
     * 多租户编号
     */
    @Column(name = "tenant_id")
    private Long tenantId;

    public String getDelFlag() {
        return null;
    }

    public T setDelFlag(String delFlag) {
        return null;
    }

    public T setTenantId(String tenantId) {
        return null;
    }

    public String getTenantId() {
        return null;
    }

    public String getVersion() {
        return null;
    }

    public T setVersion(String version) {
        return null;
    }


//    public Definition setCreateTime(Date createTime) {
//        return null;
//    }
//
//    public Definition setUpdateTime(Date updateTime) {
//        return null;
//    }
}
