package com.cmsr.onebase.module.bpm.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程抄送记录表 表定义层。
 *
 * @author liyang
 * @since 2025-11-28
 */
public class BpmFlowCcRecordTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程抄送记录表
     */
    public static final BpmFlowCcRecordTableDef BPM_FLOW_CC_RECORD = new BpmFlowCcRecordTableDef();

    /**
     * 主键id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 任务表id
     */
    public final QueryColumn TASK_ID = new QueryColumn(this, "task_id");

    /**
     * 抄送用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 已阅 0，否 1，是
     */
    public final QueryColumn VIEWED = new QueryColumn(this, "viewed");

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
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 流程实例id
     */
    public final QueryColumn INSTANCE_ID = new QueryColumn(this, "instance_id");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 已读时间
     */
    public final QueryColumn VIEWED_TIME = new QueryColumn(this, "viewed_time");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, INSTANCE_ID, TASK_ID, VIEWED, VIEWED_TIME, USER_ID, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public BpmFlowCcRecordTableDef() {
        super("", "bpm_flow_cc_record");
    }

    private BpmFlowCcRecordTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BpmFlowCcRecordTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BpmFlowCcRecordTableDef("", "bpm_flow_cc_record", alias));
    }

}
