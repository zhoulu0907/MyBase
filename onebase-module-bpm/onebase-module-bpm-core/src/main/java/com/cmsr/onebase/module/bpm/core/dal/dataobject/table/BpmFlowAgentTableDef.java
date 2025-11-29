package com.cmsr.onebase.module.bpm.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程代理表 表定义层。
 *
 * @author liyang
 * @since 2025-11-28
 */
public class BpmFlowAgentTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程代理表
     */
    public static final BpmFlowAgentTableDef BPM_FLOW_AGENT = new BpmFlowAgentTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 应用ID
     */
    public final QueryColumn APP_ID = new QueryColumn(this, "app_id");

    /**
     * 代理人用户ID，接受委托代为处理流程任务
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
     * 代理结束时间，必须晚于开始时间
     */
    public final QueryColumn END_TIME = new QueryColumn(this, "end_time");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 租户ID
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 代理人用户名称
     */
    public final QueryColumn AGENT_NAME = new QueryColumn(this, "agent_name");

    /**
     * 撤销人
     */
    public final QueryColumn REVOKER_ID = new QueryColumn(this, "revoker_id");

    /**
     * 代理生效开始时间
     */
    public final QueryColumn START_TIME = new QueryColumn(this, "start_time");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 被代理人用户ID（即委托人），代理关系的发起方
     */
    public final QueryColumn PRINCIPAL_ID = new QueryColumn(this, "principal_id");

    /**
     * 撤销时间
     */
    public final QueryColumn REVOKED_TIME = new QueryColumn(this, "revoked_time");

    /**
     * 被代理人用户名称（即委托人）
     */
    public final QueryColumn PRINCIPAL_NAME = new QueryColumn(this, "principal_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APP_ID, PRINCIPAL_ID, PRINCIPAL_NAME, AGENT_ID, AGENT_NAME, START_TIME, END_TIME, REVOKER_ID, REVOKED_TIME, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public BpmFlowAgentTableDef() {
        super("", "bpm_flow_agent");
    }

    private BpmFlowAgentTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BpmFlowAgentTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BpmFlowAgentTableDef("", "bpm_flow_agent", alias));
    }

}
