package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeCategoryDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class FlowNodeCategoryRepository extends DataRepository<FlowNodeCategoryDO> {

    public FlowNodeCategoryRepository() {
        super(FlowNodeCategoryDO.class);
    }

    public List<FlowNodeCategoryDO> findAllCategories() {
        ConfigStore configs = new DefaultConfigStore();
        configs.order("sort_order", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

}
