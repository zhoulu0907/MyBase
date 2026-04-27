package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-26
 */
public class EtlScheduleJobTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlScheduleJobTableDef ETL_SCHEDULE_JOB = new EtlScheduleJobTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn JOB_ID = new QueryColumn(this, "job_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn JOB_STATUS = new QueryColumn(this, "job_status");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LAST_JOB_TIME = new QueryColumn(this, "last_job_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn WORKFLOW_UUID = new QueryColumn(this, "workflow_uuid");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn LAST_SUCCESS_TIME = new QueryColumn(this, "last_success_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, WORKFLOW_UUID, JOB_ID, JOB_STATUS, LAST_JOB_TIME, LAST_SUCCESS_TIME, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION, TENANT_ID};

    public EtlScheduleJobTableDef() {
        super("", "etl_schedule_job");
    }

    private EtlScheduleJobTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlScheduleJobTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlScheduleJobTableDef("", "etl_schedule_job", alias));
    }

}
