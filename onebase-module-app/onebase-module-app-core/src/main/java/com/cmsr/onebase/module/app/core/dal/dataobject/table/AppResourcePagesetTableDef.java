package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-25
 */
public class AppResourcePagesetTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppResourcePagesetTableDef APP_RESOURCE_PAGESET = new AppResourcePagesetTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn MENU_ID = new QueryColumn(this, "menu_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    
    public final QueryColumn DISPLAY_NAME = new QueryColumn(this, "display_name");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn PAGESET_CODE = new QueryColumn(this, "pageset_code");

    
    public final QueryColumn PAGESET_NAME = new QueryColumn(this, "pageset_name");

    
    public final QueryColumn PAGESET_TYPE = new QueryColumn(this, "pageset_type");

    
    public final QueryColumn MAIN_METADATA = new QueryColumn(this, "main_metadata");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{PAGESET_CODE, MENU_ID, MAIN_METADATA, PAGESET_NAME, DISPLAY_NAME, DESCRIPTION, TENANT_ID, ID, CREATE_TIME, UPDATE_TIME, CREATOR, UPDATER, DELETED, LOCK_VERSION, PAGESET_TYPE};

    public AppResourcePagesetTableDef() {
        super("", "app_resource_pageset");
    }

    private AppResourcePagesetTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppResourcePagesetTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppResourcePagesetTableDef("", "app_resource_pageset", alias));
    }

}
