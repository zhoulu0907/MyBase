package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 数据权限-权限组配置表 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppAuthDataGroupTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 数据权限-权限组配置表
     */
    public static final AppAuthDataGroupTableDef APP_AUTH_DATA_GROUP = new AppAuthDataGroupTableDef();

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

    /**
     * 组名称
     */
    public final QueryColumn GROUP_NAME = new QueryColumn(this, "group_name");

    
    public final QueryColumn SCOPE_TAGS = new QueryColumn(this, "scope_tags");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 数据过滤日志的JSON
     */
    public final QueryColumn DATA_FILTER = new QueryColumn(this, "data_filter");

    /**
     * 组排序
     */
    public final QueryColumn GROUP_ORDER = new QueryColumn(this, "group_order");

    /**
     * 关联业务实体字段对应的权限范围
     */
    public final QueryColumn SCOPE_LEVEL = new QueryColumn(this, "scope_level");

    /**
     * 关联业务实体字段对应的权限值
     */
    public final QueryColumn SCOPE_VALUE = new QueryColumn(this, "scope_value");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    /**
     * 描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 关联业务实体字段id
     */
    public final QueryColumn SCOPE_FIELD_UUID = new QueryColumn(this, "scope_field_uuid");

    /**
     * 应用id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 是否可以操作
     */
    public final QueryColumn OPERATION_TAGS = new QueryColumn(this, "operation_tags");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_TAG, ROLE_UUID, MENU_UUID, GROUP_NAME, GROUP_ORDER, DESCRIPTION, SCOPE_TAGS, SCOPE_FIELD_UUID, SCOPE_LEVEL, SCOPE_VALUE, DATA_FILTER, OPERATION_TAGS, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthDataGroupTableDef() {
        super("", "app_auth_data_group");
    }

    private AppAuthDataGroupTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthDataGroupTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthDataGroupTableDef("", "app_auth_data_group", alias));
    }

}
