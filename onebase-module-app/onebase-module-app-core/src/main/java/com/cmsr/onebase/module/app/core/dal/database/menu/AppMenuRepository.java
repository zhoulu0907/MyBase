package com.cmsr.onebase.module.app.core.dal.database.menu;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppMenuRepository extends DataRepository<MenuDO> {

    public AppMenuRepository() {
        super(MenuDO.class);
    }

    public List<MenuDO> findByApplicationIdAndType(Long applicationId, Set<Integer> menuTypes) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order("menu_sort", Order.TYPE.ASC);
        configs.in("menu_type", menuTypes);

        return findAllByConfig(configs);
    }

    public List<MenuDO> findByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order("menu_sort", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public long countByParentId(Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("parent_id", id);
        return countByConfig(configs);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(configs);
    }


    public int countByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        return (int) countByConfig(configs);
    }

    public List<MenuDO> findVisibleByAppId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("is_visible", 1);
        return findAllByConfig(configs);
    }

    public List<MenuDO> findVisibleByAppIdAndMenuIds(Long applicationId, Set<Long> menuIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.in("id", menuIds);
        configs.eq("is_visible", 1);
        return findAllByConfig(configs);
    }

    public Set<Long> findPageIdsByAppIdAndMenuId(Long applicationId, Long menuId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.param("applicationId", applicationId);
        configs.param("menuId", menuId);
        String sql = """
                select
                	a.id as menu_id,
                	c.page_id
                from
                	app_resource_pageset_page c
                left join app_resource_pageset b on
                	c.pageset_id = b.id
                left join app_menu a on
                	b.menu_id = a.id
                where
                	a.deleted = 0
                	and b.deleted = 0
                	and c.deleted = 0
                	and a.is_visible = 1
                	and a.application_id = #{applicationId}
                	and a.id = #{menuId}
                """;
        DataSet dataSet = this.querys(sql, configs);
        return dataSet.stream().map(dataRow -> dataRow.getLong("page_id")).collect(Collectors.toSet());
    }


}
