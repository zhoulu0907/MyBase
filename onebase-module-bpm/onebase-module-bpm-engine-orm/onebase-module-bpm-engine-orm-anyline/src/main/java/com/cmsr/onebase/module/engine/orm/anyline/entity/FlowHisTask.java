package com.cmsr.onebase.module.engine.orm.anyline.entity;

import com.cmsr.onebase.framework.data.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.HisTask;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WarmFlow 历史任务 DO，对应表 flow_his_task。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "bpm_flow_his_task")
public class FlowHisTask extends BaseEntity implements HisTask {
    public static final String INSTANCE_ID = "instance_id";

    public static final String DEFINITION_UUID = "definition_uuid";

    public static final String SKIP_TYPE = "skip_type";

    public static final String NODE_CODE = "node_code";

    public static final String TASK_ID = "task_id";

    public static final String COOPERATE_TYPE = "cooperate_type";

    public static final String NODE_TYPE = "node_type";

    /** 流程定义ID */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 流程定义UUID（主关联） */
    @Column(name = "definition_uuid", length = 64, nullable = false)
    private String definitionUuid;

    /** 流程实例ID */
    @Column(name = INSTANCE_ID, nullable = false)
    private Long instanceId;

    /** 待办任务ID */
    @Column(name = TASK_ID, nullable = false)
    private Long taskId;

    /** 开始节点编码 */
    @Column(name = NODE_CODE, length = 100)
    private String nodeCode;

    /** 开始节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 开始节点类型 */
    @Column(name = NODE_TYPE)
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
    @Column(name = COOPERATE_TYPE, nullable = false)
    private Integer cooperateType;

    /** 协作人 */
    @Column(name = "collaborator", length = 500)
    private String collaborator;

    /** 流转类型（PASS/REJECT/NONE） */
    @Column(name = SKIP_TYPE, length = 10)
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

    public HisTask setDefinitionUuid(String definitionUuid) {
        this.definitionUuid = definitionUuid;
        return this;
    }
}


