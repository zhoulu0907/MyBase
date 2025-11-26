package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public class FlowExecutionLogTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowExecutionLogTableDef FLOW_EXECUTION_LOG = new FlowExecutionLogTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 结束时间
     */
    public final QueryColumn END_TIME = new QueryColumn(this, "end_time");

    /**
     * 详细执行日志
     */
    public final QueryColumn LOG_TEXT = new QueryColumn(this, "log_text");

    /**
     * 跟踪Id
     */
    public final QueryColumn TRACE_ID = new QueryColumn(this, "trace_id");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 流程Id
     */
    public final QueryColumn PROCESS_ID = new QueryColumn(this, "process_id");

    /**
     * 开始时间
     */
    public final QueryColumn START_TIME = new QueryColumn(this, "start_time");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn DURATION_TIME = new QueryColumn(this, "duration_time");

    /**
     * 错误信息（执行失败时使用）
     */
    public final QueryColumn ERROR_MESSAGE = new QueryColumn(this, "error_message");

    /**
     * 应用Id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 执行uuid
     */
    public final QueryColumn EXECUTION_UUID = new QueryColumn(this, "execution_uuid");

    /**
     * 触发用户
     */
    public final QueryColumn TRIGGER_USER_ID = new QueryColumn(this, "trigger_user_id");

    /**
     * 执行结果：success-成功, failed-失败
     */
    public final QueryColumn EXECUTION_RESULT = new QueryColumn(this, "execution_result");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TRIGGER_USER_ID, TRACE_ID, EXECUTION_UUID, APPLICATION_ID, PROCESS_ID, START_TIME, END_TIME, DURATION_TIME, EXECUTION_RESULT, LOG_TEXT, ERROR_MESSAGE, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowExecutionLogTableDef() {
        super("", "flow_execution_log");
    }

    private FlowExecutionLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowExecutionLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowExecutionLogTableDef("", "flow_execution_log", alias));
    }

}
