package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataMethodExcutePlanDO;

/**
 * 执行计划服务
 */
public interface MetadataMethodExcutePlanService {

    /**
     * 通过方法编码获取启用的执行计划
     */
    MetadataMethodExcutePlanDO getEnabledByMethodCode(String methodCode);
}
