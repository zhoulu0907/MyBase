package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用功能权限-视图权限 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-25
 */
public class AppAuthViewTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用功能权限-视图权限
     */
    public static final AppAuthViewTableDef APP_AUTH_VIEW = new AppAuthViewTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 菜单id
     */
    public final QueryColumn MENU_ID = new QueryColumn(this, "menu_id");

    /**
     * 角色id
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    /**
     * 实体id
     */
    public final QueryColumn VIEW_ID = new QueryColumn(this, "view_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 是否可访问
     */
    public final QueryColumn IS_ALLOWED = new QueryColumn(this, "is_allowed");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, ROLE_ID, MENU_ID, VIEW_ID, IS_ALLOWED, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthViewTableDef() {
        super("", "app_auth_view");
    }

    private AppAuthViewTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthViewTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthViewTableDef("", "app_auth_view", alias));
    }

}
