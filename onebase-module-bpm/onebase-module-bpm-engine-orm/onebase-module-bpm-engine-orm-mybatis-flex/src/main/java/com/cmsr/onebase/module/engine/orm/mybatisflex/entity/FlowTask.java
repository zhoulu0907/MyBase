package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WarmFlow 待办任务 DO，对应表 bpm_flow_task。
 *
 * @author liyang
 * @date 2025-10-10
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_task")
public class FlowTask extends WarmFlowBaseEntity implements Task {
    /** 对应flow_definition表的id */
    @Column(value = "definition_id", comment = "对应flow_definition表的id")
    private Long definitionId;

    /** 对应flow_instance表的id */
    @Column(value = "instance_id", comment = "对应flow_instance表的id")
    private Long instanceId;

    /** 节点编码 */
    @Column(value = "node_code", comment = "节点编码")
    private String nodeCode;

    /** 节点名称 */
    @Column(value = "node_name", comment = "节点名称")
    private String nodeName;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @Column(value = "node_type", comment = "节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nodeType;

    /** 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回） */
    @Column(value = "flow_status", comment = "流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）")
    private String flowStatus;

    /** 审批表单是否自定义（Y是 N否） */
    @Column(value = "form_custom", comment = "审批表单是否自定义（Y是 N否）")
    private String formCustom;

    /** 审批表单路径 */
    @Column(value = "form_path", comment = "审批表单路径")
    private String formPath;

    /* ==================== 以下为非数据库字段 ==================== */
    /**
     * 流程名称
     */
    @Column(ignore = true)
    private String flowName;

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
     * 流程用户列表
     */
    @Column(ignore = true)
    private List<User> userList;


    /* ==================== 以下为 Task 接口方法实现 ==================== */

    @Override
    public Task setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Task setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Task setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Task setCreateBy(String createBy) {
        if (createBy != null) {
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public Task setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public Task setTenantId(String tenantId) {
        if (tenantId != null) {
            this.wfTenantId = Long.valueOf(tenantId);
        } else {
            this.wfTenantId = null;
        }

        return this;
    }

    @Override
    public Task setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}