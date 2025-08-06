package com.cmsr.onebase.module.app.dal.database.appresource;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;

@Repository
public class AppPageSetRepository extends DataRepository {
    public AppPageSetRepository() {
        super(PageSetDO.class);
    }

    public PageSetDO findPageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("menu_id", menuId);
        return findOne(PageSetDO.class, configs);
    }

    public PageSetDO findPageSetByPageSetCode(String pageSetCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_code", pageSetCode);
        return findOne(PageSetDO.class, configs);
    }

    public void deletePageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("menu_id", menuId);
        deleteByConfig(PageSetDO.class, configs);
    }



}
