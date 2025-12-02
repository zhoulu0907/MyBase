package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppVersionTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppVersionTableDef APP_VERSION = new AppVersionTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_URL = new QueryColumn(this, "version_url");

    
    public final QueryColumn ENVIRONMENT = new QueryColumn(this, "environment");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 版本名称
     */
    public final QueryColumn VERSION_NAME = new QueryColumn(this, "version_name");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn OPERATION_TYPE = new QueryColumn(this, "operation_type");

    /**
     * 版本编号
     */
    public final QueryColumn VERSION_NUMBER = new QueryColumn(this, "version_number");

    
    public final QueryColumn VERSION_DESCRIPTION = new QueryColumn(this, "version_description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_NAME, VERSION_NUMBER, VERSION_DESCRIPTION, ENVIRONMENT, OPERATION_TYPE, VERSION_URL, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppVersionTableDef() {
        super("", "app_version");
    }

    private AppVersionTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppVersionTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppVersionTableDef("", "app_version", alias));
    }

}
