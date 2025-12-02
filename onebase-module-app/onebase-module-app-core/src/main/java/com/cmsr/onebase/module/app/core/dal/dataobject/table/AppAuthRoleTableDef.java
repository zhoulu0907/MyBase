package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用角色 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppAuthRoleTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用角色
     */
    public static final AppAuthRoleTableDef APP_AUTH_ROLE = new AppAuthRoleTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");


    public final QueryColumn CREATOR = new QueryColumn(this, "creator");


    public final QueryColumn DELETED = new QueryColumn(this, "deleted");


    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 角色编码
     */
    public final QueryColumn ROLE_CODE = new QueryColumn(this, "role_code");

    /**
     * 角色名称
     */
    public final QueryColumn ROLE_NAME = new QueryColumn(this, "role_name");

    /**
     * 角色类型，1系统管理员2系统默认用户3用户定义
     */
    public final QueryColumn ROLE_TYPE = new QueryColumn(this, "role_type");


    public final QueryColumn ROLE_UUID = new QueryColumn(this, "role_uuid");


    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");


    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");


    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");


    /**
     * 描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");


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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_UUID, APPLICATION_ID, ROLE_CODE, ROLE_NAME, ROLE_TYPE, DESCRIPTION, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthRoleTableDef() {
        super("", "app_auth_role");
    }

    private AppAuthRoleTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthRoleTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthRoleTableDef("", "app_auth_role", alias));
    }

}
