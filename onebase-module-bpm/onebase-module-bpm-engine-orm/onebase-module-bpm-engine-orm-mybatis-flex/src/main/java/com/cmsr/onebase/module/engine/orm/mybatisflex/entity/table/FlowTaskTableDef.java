package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 待办任务表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowTaskTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 待办任务表
     */
    public static final FlowTaskTableDef FLOW_TASK = new FlowTaskTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

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
     * 审批表单路径
     */
    public final QueryColumn FORM_PATH = new QueryColumn(this, "form_path");

    /**
     * 节点编码
     */
    public final QueryColumn NODE_CODE = new QueryColumn(this, "node_code");

    /**
     * 节点名称
     */
    public final QueryColumn NODE_NAME = new QueryColumn(this, "node_name");

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    public final QueryColumn NODE_TYPE = new QueryColumn(this, "node_type");

    /**
     * 租户id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 创建时间
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
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 对应flow_definition表的id
     */
    public final QueryColumn DEFINITION_ID = new QueryColumn(this, "definition_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DEFINITION_ID, INSTANCE_ID, NODE_CODE, NODE_NAME, NODE_TYPE, FLOW_STATUS, FORM_CUSTOM, FORM_PATH, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowTaskTableDef() {
        super("", "bpm_flow_task");
    }

    private FlowTaskTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowTaskTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowTaskTableDef("", "bpm_flow_task", alias));
    }

}
