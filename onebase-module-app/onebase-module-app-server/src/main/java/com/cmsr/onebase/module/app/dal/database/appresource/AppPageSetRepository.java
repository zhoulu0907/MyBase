package com.cmsr.onebase.module.app.dal.database.appresource;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;

@Repository
public class AppPageSetRepository extends DataRepositoryNew<PageSetDO> {
    public AppPageSetRepository() {
        super(PageSetDO.class);
    }

    public PageSetDO findPageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("menu_id", menuId);
        return findOne(configs);
    }

    public PageSetDO findPageSetByPageSetCode(String pageSetCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_code", pageSetCode);
        return findOne(configs);
    }

    public void deletePageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("menu_id", menuId);
        deleteByConfig(configs);
    }



}
