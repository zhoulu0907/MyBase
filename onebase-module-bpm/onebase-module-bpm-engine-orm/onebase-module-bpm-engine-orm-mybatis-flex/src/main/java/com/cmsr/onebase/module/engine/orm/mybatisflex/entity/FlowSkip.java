package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Skip;

import java.time.LocalDateTime;

/**
 * WarmFlow 节点跳转关系 DO，对应表 bpm_flow_skip。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_skip")
public class FlowSkip extends WarmFlowBaseEntity implements Skip {
    /** 流程定义ID */
    @Column(value = "definition_id", comment = "流程定义ID")
    private Long definitionId;

    /** 当前节点编码 */
    @Column(value = "now_node_code", comment = "当前节点编码")
    private String nowNodeCode;

    /** 当前节点类型 */
    @Column(value = "now_node_type", comment = "当前节点类型")
    private Integer nowNodeType;

    /** 下一个节点编码 */
    @Column(value = "next_node_code", comment = "下一个节点编码")
    private String nextNodeCode;

    /** 下一个节点类型 */
    @Column(value = "next_node_type", comment = "下一个节点类型")
    private Integer nextNodeType;

    /** 跳转名称 */
    @Column(value = "skip_name", comment = "跳转名称")
    private String skipName;

    /** 跳转类型（PASS/REJECT） */
    @Column(value = "skip_type", comment = "跳转类型（PASS/REJECT）")
    private String skipType;

    /** 跳转条件 */
    @Column(value = "skip_condition", comment = "跳转条件")
    private String skipCondition;

    /** 坐标 */
    @Column(value = "coordinate", comment = "坐标")
    private String coordinate;

    /** 扩展属性 */
    @Column(value = "ext", comment = "扩展属性")
    private String ext;

    /**
     * 优先级
     */
    @Column(value = "priority", comment = "优先级")
    private Integer priority;


    /* ==================== 以下为非数据库字段 ==================== */
    /**
     * 节点ID
     */
    private Long nodeId;

    /* ==================== 以下为 Skip 接口方法实现 ==================== */

    @Override
    public Skip setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Skip setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Skip setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Skip setCreateBy(String createBy) {
        if (createBy != null) {
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public Skip setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public Skip setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public Skip setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}


