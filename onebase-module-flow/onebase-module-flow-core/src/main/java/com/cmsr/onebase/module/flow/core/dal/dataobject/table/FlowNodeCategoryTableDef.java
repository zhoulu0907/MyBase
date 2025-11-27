package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public class FlowNodeCategoryTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowNodeCategoryTableDef FLOW_NODE_CATEGORY = new FlowNodeCategoryTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn LEVEL1_CODE = new QueryColumn(this, "level1_code");

    
    public final QueryColumn LEVEL1_NAME = new QueryColumn(this, "level1_name");

    
    public final QueryColumn LEVEL2_CODE = new QueryColumn(this, "level2_code");

    
    public final QueryColumn LEVEL2_NAME = new QueryColumn(this, "level2_name");

    
    public final QueryColumn LEVEL3_CODE = new QueryColumn(this, "level3_code");

    
    public final QueryColumn LEVEL3_NAME = new QueryColumn(this, "level3_name");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, LEVEL1_CODE, LEVEL1_NAME, LEVEL2_CODE, LEVEL2_NAME, LEVEL3_CODE, LEVEL3_NAME, SORT_ORDER, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowNodeCategoryTableDef() {
        super("", "flow_node_category");
    }

    private FlowNodeCategoryTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowNodeCategoryTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowNodeCategoryTableDef("", "flow_node_category", alias));
    }

}
