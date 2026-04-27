package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-12-22
 */
public class FlowNodeConfigTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FlowNodeConfigTableDef FLOW_NODE_CONFIG = new FlowNodeConfigTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 类别编码，唯一的
     */
    public final QueryColumn NODE_CODE = new QueryColumn(this, "node_code");

    /**
     * 展示名称
     */
    public final QueryColumn NODE_NAME = new QueryColumn(this, "node_name");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    
    public final QueryColumn CONN_CONFIG = new QueryColumn(this, "conn_config");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 类别1，用于搜索
     */
    public final QueryColumn LEVEL1_CODE = new QueryColumn(this, "level1_code");

    /**
     * 类别2，用于搜索
     */
    public final QueryColumn LEVEL2_CODE = new QueryColumn(this, "level2_code");

    /**
     * 类别3，用于搜索
     */
    public final QueryColumn LEVEL3_CODE = new QueryColumn(this, "level3_code");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn ACTION_CONFIG = new QueryColumn(this, "action_config");

    /**
     * 是否开启
     */
    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");

    /**
     * 简单描述，一句话
     */
    public final QueryColumn SIMPLE_REMARK = new QueryColumn(this, "simple_remark");

    
    public final QueryColumn CONN_CONFIG_TYPE = new QueryColumn(this, "conn_config_type");

    
    public final QueryColumn ACTION_CONFIG_TYPE = new QueryColumn(this, "action_config_type");

    /**
     * 默认参数
     */
    public final QueryColumn DEFAULT_PROPERTIES = new QueryColumn(this, "default_properties");

    /**
     * 消息描述，大段文字
     */
    public final QueryColumn DETAIL_DESCRIPTION = new QueryColumn(this, "detail_description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, LEVEL1_CODE, LEVEL2_CODE, LEVEL3_CODE, NODE_NAME, NODE_CODE, SIMPLE_REMARK, DETAIL_DESCRIPTION, ACTIVE_STATUS, DEFAULT_PROPERTIES, CONN_CONFIG_TYPE, CONN_CONFIG, ACTION_CONFIG_TYPE, ACTION_CONFIG, SORT_ORDER, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowNodeConfigTableDef() {
        super("", "flow_node_config");
    }

    private FlowNodeConfigTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowNodeConfigTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowNodeConfigTableDef("", "flow_node_config", alias));
    }

}
