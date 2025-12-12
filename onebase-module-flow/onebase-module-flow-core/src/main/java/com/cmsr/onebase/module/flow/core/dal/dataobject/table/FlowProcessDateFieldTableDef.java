package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-12-12
 */
public class FlowProcessDateFieldTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowProcessDateFieldTableDef FLOW_PROCESS_DATE_FIELD = new FlowProcessDateFieldTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn JOB_ID = new QueryColumn(this, "job_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn ENTITY_ID = new QueryColumn(this, "entity_id");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn JOB_STATUS = new QueryColumn(this, "job_status");

    
    public final QueryColumn PROCESS_ID = new QueryColumn(this, "process_id");

    
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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PROCESS_ID, JOB_ID, JOB_STATUS, ENTITY_ID, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, APPLICATION_ID};

    public FlowProcessDateFieldTableDef() {
        super("", "flow_process_date_field");
    }

    private FlowProcessDateFieldTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowProcessDateFieldTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowProcessDateFieldTableDef("", "flow_process_date_field", alias));
    }

}
