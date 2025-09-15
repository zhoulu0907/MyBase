package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataMethodExcutePlanDO;

/**
 * 执行计划服务
 */
public interface MetadataMethodExcutePlanCoreService {

    /**
     * 通过方法编码获取启用的执行计划（向后兼容）
     */
    MetadataMethodExcutePlanDO getEnabledByMethodCode(String methodCode);

    /**
     * 通过方法ID获取启用的执行计划
     */
    MetadataMethodExcutePlanDO getEnabledByMethodId(Long methodId);
}
