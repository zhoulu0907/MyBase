package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageRefRouterDO;

@Repository
public class AppPageRefRouterRepository extends DataRepositoryNew<PageRefRouterDO> {
    public AppPageRefRouterRepository() {
        super(PageRefRouterDO.class);
    }

    public List<PageRefRouterDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageRefRouterDO.PAGE_ID, pageId);
        return findAllByConfig(configs);
    }
}
