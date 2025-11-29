package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程定义表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowDefinitionTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义表
     */
    public static final FlowDefinitionTableDef FLOW_DEFINITION = new FlowDefinitionTableDef();

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
     * 删除标志
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 流程版本
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 流程类别
     */
    public final QueryColumn CATEGORY = new QueryColumn(this, "category");

    /**
     * 流程编码
     */
    public final QueryColumn FLOW_CODE = new QueryColumn(this, "flow_code");

    /**
     * 流程名称
     */
    public final QueryColumn FLOW_NAME = new QueryColumn(this, "flow_name");

    /**
     * 审批表单路径
     */
    public final QueryColumn FORM_PATH = new QueryColumn(this, "form_path");

    /**
     * 租户id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    public final QueryColumn IS_PUBLISH = new QueryColumn(this, "is_publish");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    public final QueryColumn FORM_CUSTOM = new QueryColumn(this, "form_custom");

    /**
     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
     */
    public final QueryColumn MODEL_VALUE = new QueryColumn(this, "model_value");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 监听器路径
     */
    public final QueryColumn LISTENER_PATH = new QueryColumn(this, "listener_path");

    /**
     * 监听器类型
     */
    public final QueryColumn LISTENER_TYPE = new QueryColumn(this, "listener_type");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, FLOW_CODE, FLOW_NAME, MODEL_VALUE, CATEGORY, VERSION, IS_PUBLISH, FORM_CUSTOM, FORM_PATH, ACTIVITY_STATUS, LISTENER_TYPE, LISTENER_PATH, EXT, LOCK_VERSION, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, CREATOR};

    public FlowDefinitionTableDef() {
        super("", "bpm_flow_definition");
    }

    private FlowDefinitionTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowDefinitionTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowDefinitionTableDef("", "bpm_flow_definition", alias));
    }

}
