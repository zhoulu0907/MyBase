package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;

@Repository
public class AppComponentRepository extends DataRepositoryNew<ComponentDO> {
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
        return findAllByConfig(configs);
    }
}
