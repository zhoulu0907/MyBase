package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-12-20
 */
public class AppNavigationTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppNavigationTableDef APP_NAVIGATION = new AppNavigationTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn ICON_NAME = new QueryColumn(this, "icon_name");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn ICON_COLOR = new QueryColumn(this, "icon_color");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn THEME_COLOR = new QueryColumn(this, "theme_color");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    
    public final QueryColumn WEB_NAV_LAYOUT = new QueryColumn(this, "web_nav_layout");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn WEB_DEFAULT_MENU = new QueryColumn(this, "web_default_menu");

    
    public final QueryColumn MOBILE_NAV_LAYOUT = new QueryColumn(this, "mobile_nav_layout");

    
    public final QueryColumn MOBILE_DEFAULT_MENU = new QueryColumn(this, "mobile_default_menu");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_TAG, THEME_COLOR, ICON_NAME, ICON_COLOR, WEB_DEFAULT_MENU, WEB_NAV_LAYOUT, MOBILE_DEFAULT_MENU, MOBILE_NAV_LAYOUT, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppNavigationTableDef() {
        super("", "app_navigation");
    }

    private AppNavigationTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppNavigationTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppNavigationTableDef("", "app_navigation", alias));
    }

}
