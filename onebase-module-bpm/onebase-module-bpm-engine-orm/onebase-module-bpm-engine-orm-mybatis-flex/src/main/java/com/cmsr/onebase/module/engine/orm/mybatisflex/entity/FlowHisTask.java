package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.HisTask;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WarmFlow 历史任务 DO，对应表 bpm_flow_his_task。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_his_task")
public class FlowHisTask extends WarmFlowBaseEntity implements HisTask {
    /** 流程定义ID */
    @Column(value = "definition_id", comment = "流程定义ID")
    private Long definitionId;

    /** 流程实例ID */
    @Column(value = "instance_id", comment = "流程实例ID")
    private Long instanceId;

    /** 待办任务ID */
    @Column(value = "task_id", comment = "待办任务ID")
    private Long taskId;

    /** 开始节点编码 */
    @Column(value = "node_code", comment = "开始节点编码")
    private String nodeCode;

    /** 开始节点名称 */
    @Column(value = "node_name", comment = "开始节点名称")
    private String nodeName;

    /** 开始节点类型 */
    @Column(value = "node_type", comment = "开始节点类型")
    private Integer nodeType;

    /** 目标节点编码 */
    @Column(value = "target_node_code", comment = "目标节点编码")
    private String targetNodeCode;

    /** 结束节点名称 */
    @Column(value = "target_node_name", comment = "结束节点名称")
    private String targetNodeName;

    /** 审批人 */
    @Column(value = "approver", comment = "审批人")
    private String approver;

    /** 协作方式 */
    @Column(value = "cooperate_type", comment = "协作方式")
    private Integer cooperateType;

    /** 协作人 */
    @Column(value = "collaborator", comment = "协作人")
    private String collaborator;

    /** 流转类型（PASS/REJECT/NONE） */
    @Column(value = "skip_type", comment = "流转类型（PASS/REJECT/NONE）")
    private String skipType;

    /** 流程状态 */
    @Column(value = "flow_status", comment = "流程状态")
    private String flowStatus;

    /** 审批表单是否自定义（Y/N） */
    @Column(value = "form_custom", comment = "审批表单是否自定义（Y/N）")
    private String formCustom;

    /** 审批表单路径 */
    @Column(value = "form_path", comment = "审批表单路径")
    private String formPath;

    /** 审批意见 */
    @Column(value = "message", comment = "审批意见")
    private String message;

    /** 任务变量 */
    @Column(value = "variable", comment = "任务变量")
    private String variable;

    /** 业务详情 */
    @Column(value = "ext", comment = "业务详情")
    private String ext;


    /* ==================== 以下为非数据库字段 ==================== */

    /**
     * 业务id
     */
    @Column(ignore = true)
    private String businessId;

    /**
     * 权限标识 permissionFlag的list形式
     */
    @Column(ignore = true)
    private List<String> permissionList;

    /**
     * 流程名称
     */
    @Column(ignore = true)
    private String flowName;


    /* ==================== 以下为 HisTask 接口方法实现 ==================== */

    @Override
    public HisTask setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public HisTask setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public HisTask setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public HisTask setCreateBy(String createBy) {
        if (createBy != null) {
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public HisTask setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }
        return this;
    }

    @Override
    public HisTask setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public HisTask setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}


