package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;

@Repository
public class AppComponentRepository extends DataRepositoryNew<ComponentDO> {
    public AppComponentRepository() {
        super(ComponentDO.class);
    }

    public void deleteComponentByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_id", pageId);
        deleteByConfig(configs);
    }

    public List<ComponentDO> findByPageID(Long pageID){
        ConfigStore cfg = new DefaultConfigStore();
        cfg.eq("page_id", pageID);
        return findAllByConfig(cfg);
    }
}
