package com.cmsr.onebase.module.app.dal.database.menu;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppMenuRepository extends DataRepositoryNew<MenuDO> {

    public AppMenuRepository() {
        super(MenuDO.class);
    }

    public List<MenuDO> findByApplicationCode(String applicationCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        configs.order("menu_sort", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public MenuDO findByMenuCode(String menuCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("menu_code", menuCode);
        return findOne(configs);
    }

    public long countByParentId(Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("parent_id", id);
        return countByConfig(configs);
    }

    public void deleteByApplicationCode(String applicationCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        deleteByConfig(configs);
    }


    public int countByApplicationCode(String applicationCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_code", applicationCode);
        return (int) countByConfig(configs);
    }
}
