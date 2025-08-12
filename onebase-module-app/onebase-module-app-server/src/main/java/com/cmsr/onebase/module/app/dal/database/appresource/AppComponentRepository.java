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

    public void deleteComponentByPageCode(String pageCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_code", pageCode);
        deleteByConfig(configs);
    }

    public List<ComponentDO> findByPageCode(String pageCode){
        ConfigStore cfg = new DefaultConfigStore();
        cfg.eq("page_code", pageCode);
        return findAllByConfig(cfg);
    }
}
