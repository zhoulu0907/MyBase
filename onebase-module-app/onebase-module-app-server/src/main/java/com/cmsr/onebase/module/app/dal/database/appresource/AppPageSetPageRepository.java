package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;

@Repository
public class AppPageSetPageRepository extends DataRepositoryNew<PageSetPageDO> {
    public AppPageSetPageRepository() {
        super(PageSetPageDO.class);
    }

    public void deleteByPageCode(String pageCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_ref", pageCode);
        deleteByConfig(configs);

        return;
    }

    public List<PageSetPageDO> findByPageSetCode(String pageSetCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_ref", pageSetCode);
        return findAllByConfig(configs);
    }

    public PageSetPageDO findByPageSetCodeAndPageRef(String pageSetCode, String pageRef) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_ref", pageSetCode);
        configs.eq("page_ref", pageRef);
        return findOne(configs);
    }
}
