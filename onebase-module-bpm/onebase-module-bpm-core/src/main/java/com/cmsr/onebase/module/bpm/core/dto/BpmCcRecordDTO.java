package com.cmsr.onebase.module.bpm.core.dto;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抄送记录
 */
@Data
public class BpmCcRecordDTO extends BpmFlowCcRecordDO {
    /**
     * 应用ID
     */
    @Column(name = "app_id", length = 100)
    private Long appId;

    /**
     * 业务标题
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
     * 流程表单
     */
    @Column(name = "form_name", length = 100)
    private String formName;

    /**
     * 绑定的视图ID，如pageSetId
     */
    @Column(name = "binding_view_id", length = 100)
    private String bindingViewId;

    /**
     * 流程状态
     */
    @Column(name = "flow_status", length = 100)
    private String flowStatus;
}
