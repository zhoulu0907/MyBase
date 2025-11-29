package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程节点表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowNodeTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程节点表
     */
    public static final FlowNodeTableDef FLOW_NODE = new FlowNodeTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 节点扩展属性
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
     * 版本
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 审批表单路径
     */
    public final QueryColumn FORM_PATH = new QueryColumn(this, "form_path");

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
     * 流程签署比例值
     */
    public final QueryColumn NODE_RATIO = new QueryColumn(this, "node_ratio");

    /**
     * 坐标
     */
    public final QueryColumn COORDINATE = new QueryColumn(this, "coordinate");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    public final QueryColumn FORM_CUSTOM = new QueryColumn(this, "form_custom");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 任意结点跳转
     */
    public final QueryColumn ANY_NODE_SKIP = new QueryColumn(this, "any_node_skip");

    /**
     * 处理器路径
     */
    public final QueryColumn HANDLER_PATH = new QueryColumn(this, "handler_path");

    /**
     * 处理器类型
     */
    public final QueryColumn HANDLER_TYPE = new QueryColumn(this, "handler_type");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 流程定义id
     */
    public final QueryColumn DEFINITION_ID = new QueryColumn(this, "definition_id");

    /**
     * 监听器路径
     */
    public final QueryColumn LISTENER_PATH = new QueryColumn(this, "listener_path");

    /**
     * 监听器类型
     */
    public final QueryColumn LISTENER_TYPE = new QueryColumn(this, "listener_type");

    /**
     * 权限标识（权限类型:权限标识，可以多个，用@@隔开)
     */
    public final QueryColumn PERMISSION_FLAG = new QueryColumn(this, "permission_flag");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NODE_TYPE, DEFINITION_ID, NODE_CODE, NODE_NAME, PERMISSION_FLAG, NODE_RATIO, COORDINATE, ANY_NODE_SKIP, LISTENER_TYPE, LISTENER_PATH, HANDLER_TYPE, HANDLER_PATH, FORM_CUSTOM, FORM_PATH, VERSION, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, EXT, DELETED, TENANT_ID};

    public FlowNodeTableDef() {
        super("", "bpm_flow_node");
    }

    private FlowNodeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowNodeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowNodeTableDef("", "bpm_flow_node", alias));
    }

}
