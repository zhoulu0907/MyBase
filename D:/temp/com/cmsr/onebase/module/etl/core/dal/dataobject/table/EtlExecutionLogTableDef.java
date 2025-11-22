package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
public class EtlExecutionLogTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlExecutionLogTableDef ETL_EXECUTION_LOG = new EtlExecutionLogTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn END_TIME = new QueryColumn(this, "end_time");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn START_TIME = new QueryColumn(this, "start_time");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn TASK_STATUS = new QueryColumn(this, "task_status");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn WORKFLOW_ID = new QueryColumn(this, "workflow_id");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn TRIGGER_TYPE = new QueryColumn(this, "trigger_type");

    
    public final QueryColumn TRIGGER_USER = new QueryColumn(this, "trigger_user");

    
    public final QueryColumn BUSINESS_DATE = new QueryColumn(this, "business_date");

    
    public final QueryColumn DURATION_TIME = new QueryColumn(this, "duration_time");

    
    public final QueryColumn ERROR_MESSAGE = new QueryColumn(this, "error_message");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, WORKFLOW_ID, BUSINESS_DATE, START_TIME, END_TIME, DURATION_TIME, TRIGGER_TYPE, TRIGGER_USER, TASK_STATUS, ERROR_MESSAGE, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION};

    public EtlExecutionLogTableDef() {
        super("", "etl_execution_log");
    }

    private EtlExecutionLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlExecutionLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlExecutionLogTableDef("", "etl_execution_log", alias));
    }

}
