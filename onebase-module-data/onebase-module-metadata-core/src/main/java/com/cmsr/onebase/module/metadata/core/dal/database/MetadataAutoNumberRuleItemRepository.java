package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自动编号-规则项 仓储
 */
@Repository
public class MetadataAutoNumberRuleItemRepository extends DataRepository<MetadataAutoNumberRuleItemDO> {
    public MetadataAutoNumberRuleItemRepository() { super(MetadataAutoNumberRuleItemDO.class); }

    public List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberRuleItemDO.CONFIG_ID, configId);
        cs.and("deleted", 0);
        cs.order(MetadataAutoNumberRuleItemDO.ITEM_ORDER, Order.TYPE.ASC);
        cs.order("create_time", Order.TYPE.ASC);
        return findAllByConfig(cs);
    }

    public void deleteByConfigId(Long configId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberRuleItemDO.CONFIG_ID, configId);
        deleteByConfig(cs);
    }
}


