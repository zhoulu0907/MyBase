package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public class FlowNodeTypeTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowNodeTypeTableDef FLOW_NODE_TYPE = new FlowNodeTypeTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn TYPE_CODE = new QueryColumn(this, "type_code");

    
    public final QueryColumn TYPE_NAME = new QueryColumn(this, "type_name");

    
    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn LEVEL1_CODE = new QueryColumn(this, "level1_code");

    
    public final QueryColumn LEVEL2_CODE = new QueryColumn(this, "level2_code");

    
    public final QueryColumn LEVEL3_CODE = new QueryColumn(this, "level3_code");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");

    
    public final QueryColumn SIMPLE_REMARK = new QueryColumn(this, "simple_remark");

    
    public final QueryColumn DEFAULT_PROPERTIES = new QueryColumn(this, "default_properties");

    
    public final QueryColumn DETAIL_DESCRIPTION = new QueryColumn(this, "detail_description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, LEVEL1_CODE, LEVEL2_CODE, LEVEL3_CODE, TYPE_NAME, TYPE_CODE, SIMPLE_REMARK, DETAIL_DESCRIPTION, ACTIVE_STATUS, DEFAULT_PROPERTIES, SORT_ORDER, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowNodeTypeTableDef() {
        super("", "flow_node_type");
    }

    private FlowNodeTypeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowNodeTypeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowNodeTypeTableDef("", "flow_node_type", alias));
    }

}
