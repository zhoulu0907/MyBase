package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 节点跳转关联表 表定义层。
 *
 * @author liyang
 * @since 2025-11-29
 */
public class FlowSkipTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 节点跳转关联表
     */
    public static final FlowSkipTableDef FLOW_SKIP = new FlowSkipTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 扩展信息
     */
    public final QueryColumn EXT = new QueryColumn(this, "ext");

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
     * 优先级
     */
    public final QueryColumn PRIORITY = new QueryColumn(this, "priority");

    /**
     * 跳转名称
     */
    public final QueryColumn SKIP_NAME = new QueryColumn(this, "skip_name");

    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    public final QueryColumn SKIP_TYPE = new QueryColumn(this, "skip_type");

    /**
     * 租户id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 坐标
     */
    public final QueryColumn COORDINATE = new QueryColumn(this, "coordinate");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 版本标签
     */
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 当前流程节点的编码
     */
    public final QueryColumn NOW_NODE_CODE = new QueryColumn(this, "now_node_code");

    /**
     * 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    public final QueryColumn NOW_NODE_TYPE = new QueryColumn(this, "now_node_type");

    /**
     * 流程定义id
     */
    public final QueryColumn DEFINITION_ID = new QueryColumn(this, "definition_id");

    /**
     * 下一个流程节点的编码
     */
    public final QueryColumn NEXT_NODE_CODE = new QueryColumn(this, "next_node_code");

    /**
     * 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    public final QueryColumn NEXT_NODE_TYPE = new QueryColumn(this, "next_node_type");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 跳转条件
     */
    public final QueryColumn SKIP_CONDITION = new QueryColumn(this, "skip_condition");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DEFINITION_ID, NOW_NODE_CODE, NOW_NODE_TYPE, NEXT_NODE_CODE, NEXT_NODE_TYPE, SKIP_NAME, SKIP_TYPE, SKIP_CONDITION, COORDINATE, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, EXT, PRIORITY, APPLICATION_ID, VERSION_TAG};

    public FlowSkipTableDef() {
        super("", "bpm_flow_skip");
    }

    private FlowSkipTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowSkipTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowSkipTableDef("", "bpm_flow_skip", alias));
    }

}
