package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;

@Repository
public class AppPageSetRepository extends DataRepository<PageSetDO> {

    public AppPageSetRepository() {
        super(PageSetDO.class);
    }

    public PageSetDO findPageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetDO.MENU_ID, menuId);
        return findOne(configs);
    }

    public List<PageSetDO> findByMenuIds(List<Long> menuIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(PageSetDO.MENU_ID, menuIds);
        return findAllByConfig(configs);
    }

    public void deletePageSetByMenuId(Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetDO.MENU_ID, menuId);
        deleteByConfig(configs);
    }

    public List<PageSetDO> findByMenuId(List<Long> menuIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(PageSetDO.MENU_ID, menuIds);
        return findAllByConfig(configs);
    }

}
