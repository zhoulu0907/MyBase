package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用权限-字段权限 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
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

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn MENU_UUID = new QueryColumn(this, "menu_uuid");

    
    public final QueryColumn ROLE_UUID = new QueryColumn(this, "role_uuid");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn FIELD_UUID = new QueryColumn(this, "field_uuid");

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

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    
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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_TAG, ROLE_UUID, MENU_UUID, FIELD_UUID, IS_CAN_READ, IS_CAN_EDIT, IS_CAN_DOWNLOAD, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

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
