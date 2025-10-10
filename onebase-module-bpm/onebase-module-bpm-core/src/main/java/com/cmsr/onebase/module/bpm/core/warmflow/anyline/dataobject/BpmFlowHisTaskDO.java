package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.HisTask;

import java.util.Date;
import java.util.List;

/**
 * WarmFlow 历史任务 DO，对应表 flow_his_task。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "flow_his_task")
public class BpmFlowHisTaskDO extends BpmWarmFlowBaseDO implements HisTask {

    /** 流程定义ID */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 流程实例ID */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /** 待办任务ID */
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    /** 开始节点编码 */
    @Column(name = "node_code", length = 100)
    private String nodeCode;

    /** 开始节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 开始节点类型 */
    @Column(name = "node_type")
    private Integer nodeType;

    /** 目标节点编码 */
    @Column(name = "target_node_code", length = 200)
    private String targetNodeCode;

    /** 结束节点名称 */
    @Column(name = "target_node_name", length = 200)
    private String targetNodeName;

    /** 审批人 */
    @Column(name = "approver", length = 40)
    private String approver;

    /** 协作方式 */
    @Column(name = "cooperate_type", nullable = false)
    private Integer cooperateType;

    /** 协作人 */
    @Column(name = "collaborator", length = 500)
    private String collaborator;

    /** 流转类型（PASS/REJECT/NONE） */
    @Column(name = "skip_type", length = 10, nullable = false)
    private String skipType;

    /** 流程状态 */
    @Column(name = "flow_status", length = 20, nullable = false)
    private String flowStatus;

    /** 审批表单是否自定义（Y/N） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
    private String formPath;

    /** 审批意见 */
    @Column(name = "message", length = 500)
    private String message;

    /** 任务变量 */
    @Column(name = "variable")
    private String variable;

    /** 业务详情 */
    @Column(name = "ext")
    private String ext;


    /* ==================== 以下为非数据库字段 ==================== */

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 权限标识 permissionFlag的list形式
     */
    private List<String> permissionList;

    /**
     * 流程名称
     */
    private String flowName;


    /* ==================== 以下为 HisTask 接口方法实现 ==================== */

    @Override
    public HisTask setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public HisTask setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public HisTask setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public HisTask setTenantId(String tenantId) {
        this.tenantId = Long.valueOf(tenantId);
        return this;
    }

    @Override
    public HisTask setDelFlag(String delFlag) {
        this.delFlag = Long.valueOf(delFlag);
        return this;
    }

    @Override
    public HisTask setCreateBy(String createBy) {
        this.createBy = Long.valueOf(createBy);
        return this;
    }
}


