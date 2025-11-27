package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用菜单表 表定义层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public class AppMenuTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用菜单表
     */
    public static final AppMenuTableDef APP_MENU = new AppMenuTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 实体Id
     */
    public final QueryColumn ENTITY_ID = new QueryColumn(this, "entity_id");

    /**
     * 菜单编码
     */
    public final QueryColumn MENU_CODE = new QueryColumn(this, "menu_code");

    /**
     * 菜单图标
     */
    public final QueryColumn MENU_ICON = new QueryColumn(this, "menu_icon");

    /**
     * 菜单名称
     */
    public final QueryColumn MENU_NAME = new QueryColumn(this, "menu_name");

    /**
     * 菜单排序
     */
    public final QueryColumn MENU_SORT = new QueryColumn(this, "menu_sort");

    /**
     * 菜单类型 1 页面 2 目录
     */
    public final QueryColumn MENU_TYPE = new QueryColumn(this, "menu_type");

    /**
     * 父菜单id
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 是否可见
     */
    public final QueryColumn IS_VISIBLE = new QueryColumn(this, "is_visible");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 菜单动作
     */
    public final QueryColumn ACTION_TARGET = new QueryColumn(this, "action_target");

    /**
     * 应用Id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, ENTITY_ID, PARENT_ID, MENU_CODE, MENU_SORT, MENU_TYPE, MENU_NAME, MENU_ICON, ACTION_TARGET, IS_VISIBLE, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppMenuTableDef() {
        super("", "app_menu");
    }

    private AppMenuTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppMenuTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppMenuTableDef("", "app_menu", alias));
    }

}
