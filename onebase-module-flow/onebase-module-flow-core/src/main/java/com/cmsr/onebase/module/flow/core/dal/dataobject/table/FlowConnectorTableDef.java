package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-29
 */
public class FlowConnectorTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowConnectorTableDef FLOW_CONNECTOR = new FlowConnectorTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CONFIG = new QueryColumn(this, "config");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn TYPE_CODE = new QueryColumn(this, "type_code");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn CONNECTOR_NAME = new QueryColumn(this, "connector_name");

    
    public final QueryColumn CONNECTOR_UUID = new QueryColumn(this, "connector_uuid");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CONNECTOR_UUID, CONNECTOR_NAME, TYPE_CODE, DESCRIPTION, CONFIG, APPLICATION_ID, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowConnectorTableDef() {
        super("", "flow_connector");
    }

    private FlowConnectorTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowConnectorTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowConnectorTableDef("", "flow_connector", alias));
    }

}
