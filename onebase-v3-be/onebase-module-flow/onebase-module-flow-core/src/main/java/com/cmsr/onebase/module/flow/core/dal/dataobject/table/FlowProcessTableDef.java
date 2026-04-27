package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-29
 */
public class FlowProcessTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowProcessTableDef FLOW_PROCESS = new FlowProcessTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn PROCESS_NAME = new QueryColumn(this, "process_name");

    
    public final QueryColumn PROCESS_UUID = new QueryColumn(this, "process_uuid");

    
    public final QueryColumn TRIGGER_TYPE = new QueryColumn(this, "trigger_type");

    
    public final QueryColumn ENABLE_STATUS = new QueryColumn(this, "enable_status");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn PUBLISH_STATUS = new QueryColumn(this, "publish_status");

    
    public final QueryColumn TRIGGER_CONFIG = new QueryColumn(this, "trigger_config");

    
    public final QueryColumn PROCESS_DEFINITION = new QueryColumn(this, "process_definition");

    
    public final QueryColumn PROCESS_DESCRIPTION = new QueryColumn(this, "process_description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PROCESS_UUID, PROCESS_NAME, PROCESS_DESCRIPTION, PROCESS_DEFINITION, ENABLE_STATUS, PUBLISH_STATUS, TRIGGER_TYPE, TRIGGER_CONFIG, APPLICATION_ID, VERSION_TAG, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowProcessTableDef() {
        super("", "flow_process");
    }

    private FlowProcessTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowProcessTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowProcessTableDef("", "flow_process", alias));
    }

}
