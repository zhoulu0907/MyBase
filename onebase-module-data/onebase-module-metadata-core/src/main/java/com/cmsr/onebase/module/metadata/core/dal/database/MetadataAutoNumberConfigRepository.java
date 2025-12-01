package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataAutoNumberConfigMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 自动编号-配置 仓储
 *
 * @author matianyu
 * @date 2025-11-28
 */
@Repository
public class MetadataAutoNumberConfigRepository extends ServiceImpl<MetadataAutoNumberConfigMapper, MetadataAutoNumberConfigDO> {

    public MetadataAutoNumberConfigDO findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberConfigDO::getFieldId, fieldId);
        return getOne(queryWrapper);
    }

    public void deleteByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberConfigDO::getFieldId, fieldId);
        remove(queryWrapper);
    }

    /**
     * 插入自动编号配置
     *
     * @param config 配置DO
     * @return 是否成功
     */
    public boolean insert(MetadataAutoNumberConfigDO config) {
        return save(config);
    }

    /**
     * 更新自动编号配置
     *
     * @param config 配置DO
     * @return 是否成功
     */
    public boolean update(MetadataAutoNumberConfigDO config) {
        return updateById(config);
    }
}


