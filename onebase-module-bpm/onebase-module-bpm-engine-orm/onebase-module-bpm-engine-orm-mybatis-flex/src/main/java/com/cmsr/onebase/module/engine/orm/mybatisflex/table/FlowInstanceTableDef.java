package com.cmsr.onebase.module.engine.orm.mybatisflex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程实例表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowInstanceTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程实例表
     */
    public static final FlowInstanceTableDef BPM_FLOW_INSTANCE = new FlowInstanceTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 扩展字段，预留给业务系统使用
     */
    public final QueryColumn EXT = new QueryColumn(this, "ext");

    /**
     * 创建人
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 流程定义json
     */
    public final QueryColumn DEF_JSON = new QueryColumn(this, "def_json");

    /**
     * 删除标志
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 流程节点编码
     */
    public final QueryColumn NODE_CODE = new QueryColumn(this, "node_code");

    /**
     * 流程节点名称
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
     * 任务变量
     */
    public final QueryColumn VARIABLE = new QueryColumn(this, "variable");

    /**
     * 业务id
     */
    public final QueryColumn BUSINESS_ID = new QueryColumn(this, "business_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）
     */
    public final QueryColumn FLOW_STATUS = new QueryColumn(this, "flow_status");

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
     * 流程激活状态（0挂起 1激活）
     */
    public final QueryColumn ACTIVITY_STATUS = new QueryColumn(this, "activity_status");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DEFINITION_ID, BUSINESS_ID, NODE_TYPE, NODE_CODE, NODE_NAME, VARIABLE, FLOW_STATUS, ACTIVITY_STATUS, DEF_JSON, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, EXT, DELETED, TENANT_ID};

    public FlowInstanceTableDef() {
        super("", "bpm_flow_instance");
    }

    private FlowInstanceTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowInstanceTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowInstanceTableDef("", "bpm_flow_instance", alias));
    }

}
