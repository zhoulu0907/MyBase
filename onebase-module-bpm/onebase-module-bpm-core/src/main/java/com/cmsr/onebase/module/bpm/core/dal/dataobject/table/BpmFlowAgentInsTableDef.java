package com.cmsr.onebase.module.bpm.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 代理关系实例表 表定义层。
 *
 * @author liyang
 * @since 2025-11-29
 */
public class BpmFlowAgentInsTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 代理关系实例表
     */
    public static final BpmFlowAgentInsTableDef BPM_FLOW_AGENT_INS = new BpmFlowAgentInsTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 任务ID
     */
    public final QueryColumn TASK_ID = new QueryColumn(this, "task_id");

    /**
     * 代理人ID
     */
    public final QueryColumn AGENT_ID = new QueryColumn(this, "agent_id");

    /**
     * 创建人
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 删除标志
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 租户id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 代理人用户名称
     */
    public final QueryColumn AGENT_NAME = new QueryColumn(this, "agent_name");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 流程实例ID
     */
    public final QueryColumn INSTANCE_ID = new QueryColumn(this, "instance_id");

    /**
     * 是否执行人：0=未操作, 1=执行人
     */
    public final QueryColumn IS_EXECUTOR = new QueryColumn(this, "is_executor");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 被代理人ID
     */
    public final QueryColumn PRINCIPAL_ID = new QueryColumn(this, "principal_id");

    /**
     * 被代理人用户名称
     */
    public final QueryColumn PRINCIPAL_NAME = new QueryColumn(this, "principal_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TASK_ID, INSTANCE_ID, PRINCIPAL_ID, PRINCIPAL_NAME, AGENT_ID, AGENT_NAME, IS_EXECUTOR, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public BpmFlowAgentInsTableDef() {
        super("", "bpm_flow_agent_ins");
    }

    private BpmFlowAgentInsTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BpmFlowAgentInsTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BpmFlowAgentInsTableDef("", "bpm_flow_agent_ins", alias));
    }

}
