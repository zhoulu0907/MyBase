package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public class AppVersionResourceTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppVersionResourceTableDef APP_VERSION_RESOURCE = new AppVersionResourceTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 资源数据
     */
    public final QueryColumn RES_DATA = new QueryColumn(this, "res_data");

    /**
     * 协议类型
     */
    public final QueryColumn RES_TYPE = new QueryColumn(this, "res_type");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 版本ID
     */
    public final QueryColumn VERSION_ID = new QueryColumn(this, "version_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, VERSION_ID, RES_TYPE, RES_DATA, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppVersionResourceTableDef() {
        super("", "app_version_resource");
    }

    private AppVersionResourceTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppVersionResourceTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppVersionResourceTableDef("", "app_version_resource", alias));
    }

}
