package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程实例扩展信息表 实体类。
 *
 * @author liyang
 * @since 2025-11-28
 */
@Data
@Table("bpm_flow_instance_biz_ext")
public class BpmFlowInsBizExtDO extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 业务ID
     */
    private String businessDataId;

    /**
     * 业务编码
     */
    private String businessDataCode;

    /**
     * 业务标题
     */
    private String bpmTitle;

    /**
     * 发起人ID
     */
    private String initiatorId;

    /**
     * 发起人名称（冗余字段）
     */
    private String initiatorName;

    /**
     * 发起部门ID
     */
    private Long initiatorDeptId;

    /**
     * 发起部门名称（冗余字段）
     */
    private String initiatorDeptName;

    /**
     * 发起时间（与create_time的区别：以提交表单动作为标准，而非保存表单）
     */
    private LocalDateTime submitTime;

    /**
     * 表单摘要
     */
    private String formSummary;

    /**
     * 流程表单
     */
    private String formName;

    /**
     * 流程版本号
     */
    private String bpmVersion;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 发起人头像
     */
    private String initiatorAvatar;

    /**
     * 绑定视图ID（与流程实例表的form_path字段保持一致）
     */
    private String bindingViewId;

}
