package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public class AppResourcePagesetPageTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppResourcePagesetPageTableDef APP_RESOURCE_PAGESET_PAGE = new AppResourcePagesetPageTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn PAGE_ID = new QueryColumn(this, "page_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn PAGE_TYPE = new QueryColumn(this, "page_type");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");

    
    public final QueryColumn PAGESET_ID = new QueryColumn(this, "pageset_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn DEFAULT_SEQ = new QueryColumn(this, "default_seq");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{PAGESET_ID, PAGE_ID, PAGE_TYPE, DEFAULT_SEQ, TENANT_ID, ID, CREATE_TIME, UPDATE_TIME, CREATOR, UPDATER, DELETED, LOCK_VERSION, IS_DEFAULT};

    public AppResourcePagesetPageTableDef() {
        super("", "app_resource_pageset_page");
    }

    private AppResourcePagesetPageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppResourcePagesetPageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppResourcePagesetPageTableDef("", "app_resource_pageset_page", alias));
    }

}
