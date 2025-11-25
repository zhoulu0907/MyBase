package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public class FlowConnectorScriptTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowConnectorScriptTableDef FLOW_CONNECTOR_SCRIPT = new FlowConnectorScriptTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn RAW_SCRIPT = new QueryColumn(this, "raw_script");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn SCRIPT_NAME = new QueryColumn(this, "script_name");

    
    public final QueryColumn SCRIPT_TYPE = new QueryColumn(this, "script_type");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn CONNECTOR_ID = new QueryColumn(this, "connector_id");

    
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn INPUT_PARAMETER = new QueryColumn(this, "input_parameter");

    
    public final QueryColumn OUTPUT_PARAMETER = new QueryColumn(this, "output_parameter");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CONNECTOR_ID, SCRIPT_NAME, SCRIPT_TYPE, DESCRIPTION, RAW_SCRIPT, INPUT_PARAMETER, OUTPUT_PARAMETER, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowConnectorScriptTableDef() {
        super("", "flow_connector_script");
    }

    private FlowConnectorScriptTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowConnectorScriptTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowConnectorScriptTableDef("", "flow_connector_script", alias));
    }

}
