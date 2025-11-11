package com.cmsr.onebase.module.app.core.dal.database.appresource;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<PageSetDO> findByMenuIdAndType(List<Long> menuIds, Integer pageSetType) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(PageSetDO.MENU_ID, menuIds);

        if (pageSetType != null) {
            configs.eq(PageSetDO.PAGESET_TYPE, pageSetType);
        }

        return findAllByConfig(configs);
    }

}
