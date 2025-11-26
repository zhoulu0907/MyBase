package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public class AppResourceWorkbenchComponentTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppResourceWorkbenchComponentTableDef APP_RESOURCE_WORKBENCH_COMPONENT = new AppResourceWorkbenchComponentTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CONFIG = new QueryColumn(this, "config");

    
    public final QueryColumn PAGE_ID = new QueryColumn(this, "page_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn EDIT_DATA = new QueryColumn(this, "edit_data");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn BLOCK_INDEX = new QueryColumn(this, "block_index");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn PARENT_CODE = new QueryColumn(this, "parent_code");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn COMPONENT_CODE = new QueryColumn(this, "component_code");

    
    public final QueryColumn COMPONENT_TYPE = new QueryColumn(this, "component_type");

    
    public final QueryColumn COMPONENT_INDEX = new QueryColumn(this, "component_index");

    
    public final QueryColumn CONTAINER_INDEX = new QueryColumn(this, "container_index");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{COMPONENT_CODE, PAGE_ID, COMPONENT_TYPE, CONFIG, EDIT_DATA, PARENT_CODE, BLOCK_INDEX, CONTAINER_INDEX, TENANT_ID, ID, CREATE_TIME, UPDATE_TIME, CREATOR, UPDATER, DELETED, LOCK_VERSION, COMPONENT_INDEX};

    public AppResourceWorkbenchComponentTableDef() {
        super("", "app_resource_workbench_component");
    }

    private AppResourceWorkbenchComponentTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppResourceWorkbenchComponentTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppResourceWorkbenchComponentTableDef("", "app_resource_workbench_component", alias));
    }

}
