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
public class EtlWorkflowTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlWorkflowTableDef ETL_WORKFLOW = new EtlWorkflowTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 配置信息
     */
    public final QueryColumn CONFIG = new QueryColumn(this, "config");

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
     * 启用状态,默认为关闭(0)
     */
    public final QueryColumn IS_ENABLED = new QueryColumn(this, "is_enabled");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * ETL描述
     */
    public final QueryColumn DECLARATION = new QueryColumn(this, "declaration");

    /**
     * 乐观锁版本
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 名称
     */
    public final QueryColumn WORKFLOW_NAME = new QueryColumn(this, "workflow_name");

    /**
     * 调度配置
     */
    public final QueryColumn SCHEDULE_CONFIG = new QueryColumn(this, "schedule_config");

    /**
     * 调度策略
     */
    public final QueryColumn SCHEDULE_STRATEGY = new QueryColumn(this, "schedule_strategy");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, WORKFLOW_NAME, CONFIG, IS_ENABLED, SCHEDULE_STRATEGY, SCHEDULE_CONFIG, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION, DECLARATION};

    public EtlWorkflowTableDef() {
        super("", "etl_workflow");
    }

    private EtlWorkflowTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlWorkflowTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlWorkflowTableDef("", "etl_workflow", alias));
    }

}
