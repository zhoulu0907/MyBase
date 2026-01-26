package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * flow_node_action_ref 表定义层
 * <p>
 * 定义 flow_node_action_ref 表的字段常量，用于类型安全的查询构建
 *
 * @author onebase
 * @since 2026-01-26
 */
public class FlowNodeActionRefTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 表实例
     */
    public static final FlowNodeActionRefTableDef FLOW_NODE_ACTION_REF = new FlowNodeActionRefTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 逻辑流节点ID
     */
    public final QueryColumn NODE_ID = new QueryColumn(this, "node_id");

    /**
     * 连接器实例ID
     */
    public final QueryColumn CONNECTOR_ID = new QueryColumn(this, "connector_id");

    /**
     * 动作ID
     */
    public final QueryColumn ACTION_ID = new QueryColumn(this, "action_id");

    /**
     * 引用的动作版本
     */
    public final QueryColumn ACTION_VERSION = new QueryColumn(this, "action_version");

    /**
     * 流程版本
     */
    public final QueryColumn FLOW_VERSION = new QueryColumn(this, "flow_version");

    /**
     * 创建人
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 删除标识
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 所有字段
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{
            ID, NODE_ID, CONNECTOR_ID, ACTION_ID, ACTION_VERSION, FLOW_VERSION,
            CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED
    };

    /**
     * 默认构造函数
     */
    public FlowNodeActionRefTableDef() {
        super("", "flow_node_action_ref");
    }

    /**
     * 带别名构造函数
     */
    private FlowNodeActionRefTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    /**
     * 创建别名
     */
    public FlowNodeActionRefTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowNodeActionRefTableDef("", "flow_node_action_ref", alias));
    }
}
