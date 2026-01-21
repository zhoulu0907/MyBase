package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 表定义层。
 *
 * @author zhoulu
 * @since 2026-01-16
 */
public class FlowConnectorHttpTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public static final FlowConnectorHttpTableDef FLOW_CONNECTOR_HTTP = new FlowConnectorHttpTableDef();


    public final QueryColumn ID = new QueryColumn(this, "id");


    public final QueryColumn CREATOR = new QueryColumn(this, "creator");


    public final QueryColumn DELETED = new QueryColumn(this, "deleted");


    public final QueryColumn UPDATER = new QueryColumn(this, "updater");


    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");


    public final QueryColumn CONNECTOR_UUID = new QueryColumn(this, "connector_uuid");


    public final QueryColumn HTTP_UUID = new QueryColumn(this, "http_uuid");


    public final QueryColumn HTTP_NAME = new QueryColumn(this, "http_name");


    public final QueryColumn HTTP_CODE = new QueryColumn(this, "http_code");


    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");


    public final QueryColumn REQUEST_METHOD = new QueryColumn(this, "request_method");


    public final QueryColumn REQUEST_PATH = new QueryColumn(this, "request_path");


    public final QueryColumn REQUEST_QUERY = new QueryColumn(this, "request_query");


    public final QueryColumn REQUEST_HEADERS = new QueryColumn(this, "request_headers");


    public final QueryColumn REQUEST_BODY_TYPE = new QueryColumn(this, "request_body_type");


    public final QueryColumn REQUEST_BODY_TEMPLATE = new QueryColumn(this, "request_body_template");


    public final QueryColumn AUTH_TYPE = new QueryColumn(this, "auth_type");


    public final QueryColumn AUTH_CONFIG = new QueryColumn(this, "auth_config");


    public final QueryColumn RESPONSE_MAPPING = new QueryColumn(this, "response_mapping");


    public final QueryColumn SUCCESS_CONDITION = new QueryColumn(this, "success_condition");


    public final QueryColumn INPUT_SCHEMA = new QueryColumn(this, "input_schema");


    public final QueryColumn OUTPUT_SCHEMA = new QueryColumn(this, "output_schema");


    public final QueryColumn TIMEOUT = new QueryColumn(this, "timeout");


    public final QueryColumn RETRY_COUNT = new QueryColumn(this, "retry_count");


    public final QueryColumn MOCK_RESPONSE = new QueryColumn(this, "mock_response");


    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");


    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");


    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");


    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");


    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CONNECTOR_UUID, HTTP_UUID, HTTP_NAME, HTTP_CODE, DESCRIPTION, REQUEST_METHOD, REQUEST_PATH, REQUEST_QUERY, REQUEST_HEADERS, REQUEST_BODY_TYPE, REQUEST_BODY_TEMPLATE, AUTH_TYPE, AUTH_CONFIG, RESPONSE_MAPPING, SUCCESS_CONDITION, INPUT_SCHEMA, OUTPUT_SCHEMA, TIMEOUT, RETRY_COUNT, MOCK_RESPONSE, ACTIVE_STATUS, SORT_ORDER, APPLICATION_ID, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowConnectorHttpTableDef() {
        super("", "flow_connector_http");
    }

    private FlowConnectorHttpTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowConnectorHttpTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowConnectorHttpTableDef("", "flow_connector_http", alias));
    }

}
