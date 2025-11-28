package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataAutoNumberResetLogMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自动编号-重置日志 仓储
 *
 * @author matianyu
 * @date 2025-11-28
 */
@Repository
public class MetadataAutoNumberResetLogRepository extends ServiceImpl<MetadataAutoNumberResetLogMapper, MetadataAutoNumberResetLogDO> {

    public List<MetadataAutoNumberResetLogDO> listByConfig(Long configId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberResetLogDO::getConfigId, configId)
                .eq(MetadataAutoNumberResetLogDO::getDeleted, 0)
                .orderBy(MetadataAutoNumberResetLogDO::getResetTime, false);
        return list(queryWrapper);
    }

    public void deleteByConfigId(Long configId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberResetLogDO::getConfigId, configId);
        remove(queryWrapper);
    }
}


