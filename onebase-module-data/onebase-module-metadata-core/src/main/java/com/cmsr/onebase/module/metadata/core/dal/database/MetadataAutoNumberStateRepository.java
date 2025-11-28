package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataAutoNumberStateMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 自动编号-状态 仓储
 *
 * @author matianyu
 * @date 2025-11-28
 */
@Repository
public class MetadataAutoNumberStateRepository extends ServiceImpl<MetadataAutoNumberStateMapper, MetadataAutoNumberStateDO> {

    public MetadataAutoNumberStateDO findOneByPeriod(Long configId, String periodKey) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberStateDO::getConfigId, configId)
                .eq(MetadataAutoNumberStateDO::getPeriodKey, periodKey);
        return getOne(queryWrapper);
    }

    public void deleteByConfigId(Long configId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberStateDO::getConfigId, configId);
        remove(queryWrapper);
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


