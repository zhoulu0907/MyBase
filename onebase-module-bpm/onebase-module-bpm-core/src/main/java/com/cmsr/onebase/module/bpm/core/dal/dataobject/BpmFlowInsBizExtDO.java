package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例扩展信息表
 *
 * 理论上与流程实例表是一一对应关系，但为了扩展性，这里使用扩展表
 *
 * @author liyang
 * @date 2025-10-28
 */
@Data
@Table(name = "bpm_flow_instance_biz_ext")
public class BpmFlowInsBizExtDO extends TenantBaseDO {
    public static final String INSTANCE_ID = "instance_id";

    /**
     * 流程实例ID
     */
    @Column(name = INSTANCE_ID)
    private Long instanceId;

    /**
     * 业务数据ID
     */
    @Column(name = "business_data_id", length = 100)
    private String businessDataId;

    /**
     * 业务数据编码
     */
    @Column(name = "business_data_code", length = 100)
    private String businessDataCode;

    /**
     * 绑定视图ID（与流程实例表的form_path字段保持一致）
     */
    @Column(name = "binding_view_id", length = 100)
    private String bindingViewId;

    /**
     * 应用ID
     */
    @Column(name = "app_id", length = 100)
    private Long appId;

    /**
     * 流程标题
     */
    @Column(name = "bpm_title", length = 500)
    private String bpmTitle;

    /**
     * 发起人ID
     */
    @Column(name = "initiator_id", length = 100)
    private Long initiatorId;

    /**
     * 发起人名称（冗余字段，方便查询显示）
     */
    @Column(name = "initiator_name", length = 100)
    private String initiatorName;

    /**
     * 发起人头像（冗余字段，方便查询显示）
     */
    @Column(name = "initiator_avatar", length = 500)
    private String initiatorAvatar;

    /**
     * 发起部门ID
     */
    @Column(name = "initiator_dept_id", length = 100)
    private Long initiatorDeptId;

    /**
     * 发起部门名称（冗余字段，方便查询显示）

     */
    @Column(name = "initiator_dept_name", length = 100)
    private String initiatorDeptName;

    /**
     * 发起时间
     * 与【创建时间】的区别：
     * - 发起时间以提交表单这个动作为标准
     * - 创建时间以第一次保存表单的动作为标准（可能是草稿状态，尚未提交）
     */
    @Column(name = "submit_time", length = 30)
    private LocalDateTime submitTime;

    /**
     * 表单摘要
     */
    @Column(name = "form_summary", length = 500)
    private String formSummary;

    /**
     * 表单名称
     */
    @Column(name = "form_name", length = 100)
    private String formName;

    /**
     * 流程版本号
     */
    @Column(name = "bpm_version", length = 50)
    private String bpmVersion;
}
