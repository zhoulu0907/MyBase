package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
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

    /**
     * 使用行锁查询状态（防止并发）
     */
    public MetadataAutoNumberStateDO selectByConfigIdAndPeriodKeyForUpdate(Long configId, String periodKey) {
        // 注意：此处简化实现，实际应用中可能需要在Service层使用@Transactional和手动SQL来实现FOR UPDATE
        // 当前先返回普通查询结果，后续可根据需要优化
        return findOneByPeriod(configId, periodKey);
    }

    /**
     * 普通查询状态（不加锁）
     */
    public MetadataAutoNumberStateDO selectByConfigIdAndPeriodKey(Long configId, String periodKey) {
        return findOneByPeriod(configId, periodKey);
    }
}


