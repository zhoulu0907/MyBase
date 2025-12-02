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

    /**
     * 根据配置UUID和周期键查询状态
     *
     * @param configUuid 配置UUID
     * @param periodKey 周期键
     * @return 状态对象
     */
    public MetadataAutoNumberStateDO findOneByPeriod(String configUuid, String periodKey) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberStateDO::getConfigUuid, configUuid)
                .eq(MetadataAutoNumberStateDO::getPeriodKey, periodKey);
        return getOne(queryWrapper);
    }

    /**
     * 根据配置UUID删除状态
     *
     * @param configUuid 配置UUID
     */
    public void deleteByConfigUuid(String configUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberStateDO::getConfigUuid, configUuid);
        remove(queryWrapper);
    }

    /**
     * 使用行锁查询状态（防止并发）
     *
     * @param configUuid 配置UUID
     * @param periodKey 周期键
     * @return 状态对象
     */
    public MetadataAutoNumberStateDO selectByConfigUuidAndPeriodKeyForUpdate(String configUuid, String periodKey) {
        // 注意：此处简化实现，实际应用中可能需要在Service层使用@Transactional和手动SQL来实现FOR UPDATE
        // 当前先返回普通查询结果，后续可根据需要优化
        return findOneByPeriod(configUuid, periodKey);
    }

    /**
     * 普通查询状态（不加锁）
     *
     * @param configUuid 配置UUID
     * @param periodKey 周期键
     * @return 状态对象
     */
    public MetadataAutoNumberStateDO selectByConfigUuidAndPeriodKey(String configUuid, String periodKey) {
        return findOneByPeriod(configUuid, periodKey);
    }
}


