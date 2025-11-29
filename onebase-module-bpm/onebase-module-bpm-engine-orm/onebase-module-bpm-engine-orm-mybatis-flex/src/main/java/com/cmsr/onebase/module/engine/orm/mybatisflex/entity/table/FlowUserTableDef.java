package com.cmsr.onebase.module.engine.orm.mybatisflex.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程用户表 表定义层。
 *
 * @author liyang
 * @since 2025-11-27
 */
public class FlowUserTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程用户表
     */
    public static final FlowUserTableDef FLOW_USER = new FlowUserTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

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
     * 任务表id
     */
    public final QueryColumn ASSOCIATED = new QueryColumn(this, "associated");

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
     * 权限人
     */
    public final QueryColumn PROCESSED_BY = new QueryColumn(this, "processed_by");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TYPE, PROCESSED_BY, ASSOCIATED, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public FlowUserTableDef() {
        super("", "bpm_flow_user");
    }

    private FlowUserTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FlowUserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowUserTableDef("", "bpm_flow_user", alias));
    }

}
