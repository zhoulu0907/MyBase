package com.cmsr.onebase.module.engine.orm.anyline.entity;

import com.cmsr.onebase.framework.data.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Skip;

import java.time.LocalDateTime;

/**
 * WarmFlow 节点跳转关系 DO，对应表 flow_skip。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "bpm_flow_skip")
public class FlowSkip extends BaseEntity implements Skip {
    /** 定义ID */
    public static final String DEFINITION_ID = "definition_id";

    /** 流程定义ID */
    @Column(name = DEFINITION_ID, nullable = false)
    private Long definitionId;

    /** 当前节点编码 */
    @Column(name = "now_node_code", length = 100, nullable = false)
    private String nowNodeCode;

    /** 当前节点类型 */
    @Column(name = "now_node_type")
    private Integer nowNodeType;

    /** 下一个节点编码 */
    @Column(name = "next_node_code", length = 100, nullable = false)
    private String nextNodeCode;

    /** 下一个节点类型 */
    @Column(name = "next_node_type")
    private Integer nextNodeType;

    /** 跳转名称 */
    @Column(name = "skip_name", length = 100)
    private String skipName;

    /** 跳转类型（PASS/REJECT） */
    @Column(name = "skip_type", length = 40)
    private String skipType;

    /** 跳转条件 */
    @Column(name = "skip_condition", length = 200)
    private String skipCondition;

    /** 坐标 */
    @Column(name = "coordinate", length = 100)
    private String coordinate;

    /** 扩展属性 */
    @Column(name = "ext")
    private String ext;

    /**
     * 优先级
     */
    @Column(name = "priority")
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


