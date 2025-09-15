package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataMethodExcutePlanDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * Repository for metadata_method_excute_plan
 */
@Repository
public class MetadataMethodExcutePlanRepository extends DataRepository<MetadataMethodExcutePlanDO> {

    public MetadataMethodExcutePlanRepository() {
        super(MetadataMethodExcutePlanDO.class);
    }

    /**
     * 获取启用的执行计划（按 method_id）
     */
    public MetadataMethodExcutePlanDO getEnabledByMethodId(Long methodId) {
        ConfigStore store = new DefaultConfigStore();
        store.and(Compare.EQUAL, MetadataMethodExcutePlanDO.METHOD_ID, methodId);
        store.and(Compare.EQUAL, MetadataMethodExcutePlanDO.ENABLED, 1);
        return findOne(store);
    }
}
