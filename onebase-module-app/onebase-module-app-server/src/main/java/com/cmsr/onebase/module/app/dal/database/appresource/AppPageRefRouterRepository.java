package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageRefRouterDO;

@Repository
public class AppPageRefRouterRepository extends DataRepositoryNew<PageRefRouterDO> {
    public AppPageRefRouterRepository() {
        super(PageRefRouterDO.class);
    }

    public List<PageRefRouterDO> findPageRefRouterByPageCode(String pageCode){
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_ref", pageCode);
        return findAllByConfig(configs);
    }
}
