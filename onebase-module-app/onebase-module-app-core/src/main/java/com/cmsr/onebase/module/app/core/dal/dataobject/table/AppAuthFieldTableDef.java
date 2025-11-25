package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用权限-字段权限 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-25
 */
public class AppAuthFieldTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用权限-字段权限
     */
    public static final AppAuthFieldTableDef APP_AUTH_FIELD = new AppAuthFieldTableDef();

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

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 字段id
     */
    public final QueryColumn FIELD_ID = new QueryColumn(this, "field_id");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 是否可编辑
     */
    public final QueryColumn IS_CAN_EDIT = new QueryColumn(this, "is_can_edit");

    /**
     * 是否可阅读
     */
    public final QueryColumn IS_CAN_READ = new QueryColumn(this, "is_can_read");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 是否可下载
     */
    public final QueryColumn IS_CAN_DOWNLOAD = new QueryColumn(this, "is_can_download");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, ROLE_ID, MENU_ID, FIELD_ID, IS_CAN_READ, IS_CAN_EDIT, IS_CAN_DOWNLOAD, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthFieldTableDef() {
        super("", "app_auth_field");
    }

    private AppAuthFieldTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthFieldTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthFieldTableDef("", "app_auth_field", alias));
    }

}
