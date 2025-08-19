package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberStateDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 自动编号-状态 仓储
 */
@Repository
public class MetadataAutoNumberStateRepository extends DataRepository<MetadataAutoNumberStateDO> {
    public MetadataAutoNumberStateRepository() { super(MetadataAutoNumberStateDO.class); }

    public MetadataAutoNumberStateDO findOneByPeriod(Long configId, String periodKey) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberStateDO.CONFIG_ID, configId);
        cs.and(MetadataAutoNumberStateDO.PERIOD_KEY, periodKey);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public void deleteByConfigId(Long configId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberStateDO.CONFIG_ID, configId);
        deleteByConfig(cs);
    }
}


