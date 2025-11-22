package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * ETL任务作业表 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
public class EtlScheduleJobTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ETL任务作业表
     */
    public static final EtlScheduleJobTableDef ETL_SCHEDULE_JOB = new EtlScheduleJobTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 工作流调度编码
     */
    public final QueryColumn JOB_ID = new QueryColumn(this, "job_id");

    /**
     * 创建人
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除（逻辑删除）
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 状态
     */
    public final QueryColumn JOB_STATUS = new QueryColumn(this, "job_status");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 工作流Id
     */
    public final QueryColumn WORKFLOW_ID = new QueryColumn(this, "workflow_id");

    /**
     * 最近一次执行时间
     */
    public final QueryColumn LAST_JOB_TIME = new QueryColumn(this, "last_job_time");

    /**
     * 乐观锁版本
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用Id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 最近一次成功执行时间
     */
    public final QueryColumn LAST_SUCCESS_TIME = new QueryColumn(this, "last_success_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, WORKFLOW_ID, JOB_ID, JOB_STATUS, LAST_JOB_TIME, LAST_SUCCESS_TIME, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION};

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
