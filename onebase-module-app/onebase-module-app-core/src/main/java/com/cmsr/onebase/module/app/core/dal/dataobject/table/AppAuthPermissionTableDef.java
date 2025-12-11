package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用权限-基础总表 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppAuthPermissionTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用权限-基础总表
     */
    public static final AppAuthPermissionTableDef APP_AUTH_PERMISSION = new AppAuthPermissionTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn MENU_UUID = new QueryColumn(this, "menu_uuid");

    
    public final QueryColumn ROLE_UUID = new QueryColumn(this, "role_uuid");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 页面是否可访问
     */
    public final QueryColumn IS_PAGE_ALLOWED = new QueryColumn(this, "is_page_allowed");

    /**
     * 操作权限标签
     */
    public final QueryColumn OPERATION_TAGS = new QueryColumn(this, "operation_tags");

    /**
     * 所有视图可访问
     */
    public final QueryColumn IS_ALL_VIEWS_ALLOWED = new QueryColumn(this, "is_all_views_allowed");

    /**
     * 所有字段可操作
     */
    public final QueryColumn IS_ALL_FIELDS_ALLOWED = new QueryColumn(this, "is_all_fields_allowed");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_TAG, ROLE_UUID, MENU_UUID, IS_PAGE_ALLOWED, IS_ALL_VIEWS_ALLOWED, IS_ALL_FIELDS_ALLOWED, OPERATION_TAGS, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthPermissionTableDef() {
        super("", "app_auth_permission");
    }

    private AppAuthPermissionTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthPermissionTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthPermissionTableDef("", "app_auth_permission", alias));
    }

}
