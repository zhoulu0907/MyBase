package com.cmsr.onebase.module.bpm.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 流程实例扩展信息表 表定义层。
 *
 * @author liyang
 * @since 2025-11-29
 */
public class BpmFlowInsBizExtTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 流程实例扩展信息表
     */
    public static final BpmFlowInsBizExtTableDef BPM_FLOW_INSTANCE_BIZ_EXT = new BpmFlowInsBizExtTableDef();

    /**
     * 主键ID
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
     * 业务标题
     */
    public final QueryColumn BPM_TITLE = new QueryColumn(this, "bpm_title");

    /**
     * 流程表单
     */
    public final QueryColumn FORM_NAME = new QueryColumn(this, "form_name");

    /**
     * 租户ID
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 流程版本号
     */
    public final QueryColumn BPM_VERSION = new QueryColumn(this, "bpm_version");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 流程实例ID
     */
    public final QueryColumn INSTANCE_ID = new QueryColumn(this, "instance_id");

    /**
     * 发起时间（与create_time的区别：以提交表单动作为标准，而非保存表单）
     */
    public final QueryColumn SUBMIT_TIME = new QueryColumn(this, "submit_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 表单摘要
     */
    public final QueryColumn FORM_SUMMARY = new QueryColumn(this, "form_summary");

    /**
     * 发起人ID
     */
    public final QueryColumn INITIATOR_ID = new QueryColumn(this, "initiator_id");

    /**
     * 乐观锁
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 绑定视图ID（与流程实例表的form_path字段保持一致）
     */
    public final QueryColumn BINDING_VIEW_ID = new QueryColumn(this, "binding_view_id");

    /**
     * 发起人名称（冗余字段）
     */
    public final QueryColumn INITIATOR_NAME = new QueryColumn(this, "initiator_name");

    /**
     * 业务ID
     */
    public final QueryColumn BUSINESS_DATA_ID = new QueryColumn(this, "business_data_id");

    /**
     * 发起人头像
     */
    public final QueryColumn INITIATOR_AVATAR = new QueryColumn(this, "initiator_avatar");

    /**
     * 发起部门ID
     */
    public final QueryColumn INITIATOR_DEPT_ID = new QueryColumn(this, "initiator_dept_id");

    /**
     * 业务编码
     */
    public final QueryColumn BUSINESS_DATA_CODE = new QueryColumn(this, "business_data_code");

    /**
     * 发起部门名称（冗余字段）
     */
    public final QueryColumn INITIATOR_DEPT_NAME = new QueryColumn(this, "initiator_dept_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, INSTANCE_ID, BUSINESS_DATA_ID, BUSINESS_DATA_CODE, BPM_TITLE, INITIATOR_ID, INITIATOR_NAME, INITIATOR_DEPT_ID, INITIATOR_DEPT_NAME, SUBMIT_TIME, FORM_SUMMARY, FORM_NAME, BPM_VERSION, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, APPLICATION_ID, INITIATOR_AVATAR, BINDING_VIEW_ID};

    public BpmFlowInsBizExtTableDef() {
        super("", "bpm_flow_instance_biz_ext");
    }

    private BpmFlowInsBizExtTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BpmFlowInsBizExtTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BpmFlowInsBizExtTableDef("", "bpm_flow_instance_biz_ext", alias));
    }

}
