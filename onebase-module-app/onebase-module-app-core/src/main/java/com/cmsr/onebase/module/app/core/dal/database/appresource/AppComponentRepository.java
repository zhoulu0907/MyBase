package com.cmsr.onebase.module.app.core.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.ComponentDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;

@Repository
public class AppComponentRepository extends DataRepository<ComponentDO> {
    public AppComponentRepository() {
        super(ComponentDO.class);
    }

    public void deleteComponentByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(ComponentDO.PAGE_ID, pageId);
        deleteByConfig(configs);
    }

    public List<ComponentDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(ComponentDO.PAGE_ID, pageId);
        configs.order(ComponentDO.COMPONENT_INDEX, Order.TYPE.ASC);
        return findAllByConfig(configs);
    }
}
