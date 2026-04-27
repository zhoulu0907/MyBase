package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 历史任务记录表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowHisTaskTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 历史任务记录表
     */
    public static final FlowHisTaskTableDef FLOW_HIS_TASK = new FlowHisTaskTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 扩展字段，预留给业务系统使用
     */
    public final QueryColumn EXT = new QueryColumn(this, "ext");

    /**
     * 对应flow_task表的id
     */
    public final QueryColumn TASK_ID = new QueryColumn(this, "task_id");


    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 删除标志
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 审批意见
     */
    public final QueryColumn MESSAGE = new QueryColumn(this, "message");


    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 审批者
     */
    public final QueryColumn APPROVER = new QueryColumn(this, "approver");

    /**
     * 审批表单路径
     */
    public final QueryColumn FORM_PATH = new QueryColumn(this, "form_path");

    /**
     * 开始节点编码
     */
    public final QueryColumn NODE_CODE = new QueryColumn(this, "node_code");

    /**
     * 开始节点名称
     */
    public final QueryColumn NODE_NAME = new QueryColumn(this, "node_name");

    /**
     * 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    public final QueryColumn NODE_TYPE = new QueryColumn(this, "node_type");

    /**
     * 流转类型（PASS通过 REJECT退回 NONE无动作）
     */
    public final QueryColumn SKIP_TYPE = new QueryColumn(this, "skip_type");

    /**
     * 租户id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 任务变量
     */
    public final QueryColumn VARIABLE = new QueryColumn(this, "variable");

    /**
     * 任务开始时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）
     */
    public final QueryColumn FLOW_STATUS = new QueryColumn(this, "flow_status");

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    public final QueryColumn FORM_CUSTOM = new QueryColumn(this, "form_custom");

    /**
     * 对应flow_instance表的id
     */
    public final QueryColumn INSTANCE_ID = new QueryColumn(this, "instance_id");

    /**
     * 审批完成时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 协作人
     */
    public final QueryColumn COLLABORATOR = new QueryColumn(this, "collaborator");

    /**
     * 对应flow_definition表的id
     */
    public final QueryColumn DEFINITION_ID = new QueryColumn(this, "definition_id");

    /**
     * 流程定义UUID
     */
    public final QueryColumn DEFINITION_UUID = new QueryColumn(this, "definition_uuid");

    /**
     * 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
     */
    public final QueryColumn COOPERATE_TYPE = new QueryColumn(this, "cooperate_type");

    /**
     * 目标节点编码
     */
    public final QueryColumn TARGET_NODE_CODE = new QueryColumn(this, "target_node_code");

    /**
     * 结束节点名称
     */
    public final QueryColumn TARGET_NODE_NAME = new QueryColumn(this, "target_node_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DEFINITION_ID, DEFINITION_UUID, INSTANCE_ID, TASK_ID, NODE_CODE, NODE_NAME, NODE_TYPE, TARGET_NODE_CODE, TARGET_NODE_NAME, APPROVER, COOPERATE_TYPE, COLLABORATOR, SKIP_TYPE, FLOW_STATUS, FORM_CUSTOM, FORM_PATH, EXT, MESSAGE, VARIABLE, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowHisTaskTableDef() {
        super("", "bpm_flow_his_task");
    }

    private FlowHisTaskTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowHisTaskTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowHisTaskTableDef("", "bpm_flow_his_task", alias));
    }

}
