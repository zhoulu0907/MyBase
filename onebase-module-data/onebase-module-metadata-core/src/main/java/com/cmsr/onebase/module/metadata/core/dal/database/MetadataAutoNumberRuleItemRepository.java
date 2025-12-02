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

    /**
     * 根据配置UUID获取规则项列表
     *
     * @param configUuid 配置UUID
     * @return 规则项列表
     */
    public List<MetadataAutoNumberRuleItemDO> listByConfigUuid(String configUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberRuleItemDO::getConfigUuid, configUuid)
                .orderBy(MetadataAutoNumberRuleItemDO::getItemOrder, true)
                .orderBy(MetadataAutoNumberRuleItemDO::getCreateTime, true);
        return list(queryWrapper);
    }

    /**
     * 根据配置UUID删除规则项
     *
     * @param configUuid 配置UUID
     */
    public void deleteByConfigUuid(String configUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAutoNumberRuleItemDO::getConfigUuid, configUuid);
        remove(queryWrapper);
    }
}


