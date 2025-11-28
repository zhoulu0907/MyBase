package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataAutoNumberRuleItemMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自动编号-规则项 仓储
 *
 * @author matianyu
 * @date 2025-11-28
 */
@Repository
public class MetadataAutoNumberRuleItemRepository extends ServiceImpl<MetadataAutoNumberRuleItemMapper, MetadataAutoNumberRuleItemDO> {

    public List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberRuleItemDO::getConfigId, configId)
                .eq(MetadataAutoNumberRuleItemDO::getDeleted, 0)
                .orderBy(MetadataAutoNumberRuleItemDO::getItemOrder, true)
                .orderBy(MetadataAutoNumberRuleItemDO::getCreateTime, true);
        return list(queryWrapper);
    }

    public void deleteByConfigId(Long configId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberRuleItemDO::getConfigId, configId);
        remove(queryWrapper);
    }
}


