package com.cmsr.onebase.module.engine.orm.anyline.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WarmFlow 待办任务 DO，对应表 flow_task。
 *
 * @author liyang
 * @date 2025-01-27
 */
@Data
@Accessors(chain = true)
@Table(name = "flow_task")
public class FlowTask extends BaseEntity implements Task {

    /** 对应flow_definition表的id */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 对应flow_instance表的id */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /** 节点编码 */
    @Column(name = "node_code", length = 100, nullable = false)
    private String nodeCode;

    /** 节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @Column(name = "node_type", nullable = false)
    private Integer nodeType;

    /** 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回） */
    @Column(name = "flow_status", length = 20, nullable = false)
    private String flowStatus;

    /** 审批表单是否自定义（Y是 N否） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
    private String formPath;

    /* ==================== 以下为非数据库字段 ==================== */
    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 业务id
     */
    private String businessId;


    /**
     * 权限标识 permissionFlag的list形式
     */
    private List<String> permissionList;

    /**
     * 流程用户列表
     */
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
            this.createBy = Long.valueOf(createBy);
        } else {
            this.createBy = null;
        }

        return this;
    }

    @Override
    public Task setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updateBy = Long.valueOf(updateBy);
        } else {
            this.updateBy = null;
        }

        return this;
    }

    @Override
    public Task setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public Task setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.delFlag = Long.valueOf(delFlag);
        } else {
            this.delFlag = null;
        }

        return this;
    }
}