package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Instance;

import java.time.LocalDateTime;

/**
 * WarmFlow 流程实例 DO，对应表 bpm_flow_instance。
 *
 * @author liyang
 * @date 2025-10-10
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_instance")
public class FlowInstance extends WarmFlowBaseEntity implements Instance {
    /** 对应flow_definition表的id */
    @Column(value = "definition_id", comment = "对应flow_definition表的id")
    private Long definitionId;

    /** 业务id */
    @Column(value = "business_id", comment = "业务id")
    private String businessId;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @Column(value = "node_type", comment = "节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nodeType;

    /** 流程节点编码 */
    @Column(value = "node_code", comment = "流程节点编码")
    private String nodeCode;

    /** 流程节点名称 */
    @Column(value = "node_name", comment = "流程节点名称")
    private String nodeName;

    /** 任务变量 */
    @Column(value = "variable", comment = "任务变量")
    private String variable;

    /** 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回） */
    @Column(value = "flow_status", comment = "流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）")
    private String flowStatus;

    /** 流程激活状态（0挂起 1激活） */
    @Column(value = "activity_status", comment = "流程激活状态（0挂起 1激活）")
    private Integer activityStatus;

    /** 流程定义json */
    @Column(value = "def_json", comment = "流程定义json")
    private String defJson;

    /** 扩展字段，预留给业务系统使用 */
    @Column(value = "ext", comment = "扩展字段，预留给业务系统使用")
    private String ext;

    /* ==================== 以下为非数据库字段 ==================== */

    /**
     *  审批表单是否自定义（Y是 N否）
     *  */
    @Column(ignore = true)
    private String formCustom;

    /**
     * 审批表单路径
     * */
    @Column(ignore = true)
    private String formPath;

    /**
     * 流程名称
     * */
    @Column(ignore = true)
    private String flowName;


    /* ==================== 以下为 Instance 接口方法实现 ==================== */

    @Override
    public Instance setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Instance setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Instance setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Instance setCreateBy(String createBy) {
        if (createBy != null) {
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public Instance setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public Instance setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public Instance setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}