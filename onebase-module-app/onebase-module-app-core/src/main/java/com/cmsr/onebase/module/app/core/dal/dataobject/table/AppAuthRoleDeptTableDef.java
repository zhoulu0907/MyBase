package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 角色部门表 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-25
 */
public class AppAuthRoleDeptTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 角色部门表
     */
    public static final AppAuthRoleDeptTableDef APP_AUTH_ROLE_DEPT = new AppAuthRoleDeptTableDef();

    /**
     * 主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 部门Id
     */
    public final QueryColumn DEPT_ID = new QueryColumn(this, "dept_id");

    /**
     * 角色Id
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 是否包含子部门
     */
    public final QueryColumn IS_INCLUDE_CHILD = new QueryColumn(this, "is_include_child");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, DEPT_ID, IS_INCLUDE_CHILD, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppAuthRoleDeptTableDef() {
        super("", "app_auth_role_dept");
    }

    private AppAuthRoleDeptTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppAuthRoleDeptTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppAuthRoleDeptTableDef("", "app_auth_role_dept", alias));
    }

}
