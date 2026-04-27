package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 统一动作配置表定义
 *
 * @author onebase
 * @since 2026-03-19
 */
public class FlowConnectorActionTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    public static final FlowConnectorActionTableDef FLOW_CONNECTOR_ACTION = new FlowConnectorActionTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    public final QueryColumn CONNECTOR_UUID = new QueryColumn(this, "connector_uuid");

    public final QueryColumn CONNECTOR_TYPE = new QueryColumn(this, "connector_type");

    public final QueryColumn ACTION_UUID = new QueryColumn(this, "action_uuid");

    public final QueryColumn ACTION_CODE = new QueryColumn(this, "action_code");

    public final QueryColumn ACTION_NAME = new QueryColumn(this, "action_name");

    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    public final QueryColumn INPUT_SCHEMA = new QueryColumn(this, "input_schema");

    public final QueryColumn OUTPUT_SCHEMA = new QueryColumn(this, "output_schema");

    public final QueryColumn ACTION_CONFIG = new QueryColumn(this, "action_config");

    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");

    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 所有字段
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或 large 等字段
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{
            ID, APPLICATION_ID, CONNECTOR_UUID, CONNECTOR_TYPE,
            ACTION_UUID, ACTION_CODE, ACTION_NAME, DESCRIPTION,
            INPUT_SCHEMA, OUTPUT_SCHEMA, ACTION_CONFIG,
            ACTIVE_STATUS, SORT_ORDER,
            CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID
    };

    public FlowConnectorActionTableDef() {
        super("", "flow_connector_action");
    }

    private FlowConnectorActionTableDef(String schema, String name, String alias) {
        super(schema, name, alias);
    }

    public FlowConnectorActionTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowConnectorActionTableDef("", "flow_connector_action", alias));
    }
}