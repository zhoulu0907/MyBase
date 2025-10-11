package com.cmsr.onebase.module.engine.orm.anyline.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WarmFlow 流程节点 DO，对应表 flow_node。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "bpm_flow_node")
public class FlowNode extends BaseEntity implements Node {

    /** 节点类型（0开始 1中间 2结束 3互斥网关 4并行网关） */
    @Column(name = "node_type", nullable = false)
    private Integer nodeType;

    /** 流程定义ID */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 节点编码 */
    @Column(name = "node_code", length = 100, nullable = false)
    private String nodeCode;

    /** 节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 权限标识（如 @@role:2） */
    @Column(name = "permission_flag", length = 200)
    private String permissionFlag;

    /** 签署比例值 */
    @Column(name = "node_ratio", precision = 6, scale = 3)
    private BigDecimal nodeRatio;

    /** 坐标 */
    @Column(name = "coordinate", length = 100)
    private String coordinate;

    /** 任意节点跳转 */
    @Column(name = "any_node_skip", length = 100)
    private String anyNodeSkip;

    /** 监听器类型 */
    @Column(name = "listener_type", length = 100)
    private String listenerType;

    /** 监听器路径 */
    @Column(name = "listener_path", length = 400)
    private String listenerPath;

    /** 处理器类型 */
    @Column(name = "handler_type", length = 100)
    private String handlerType;

    /** 处理器路径 */
    @Column(name = "handler_path", length = 400)
    private String handlerPath;

    /** 审批表单是否自定义（Y/N） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
    private String formPath;

    /** 扩展属性 */
    @Column(name = "ext")
    private String ext;

    /**
     * 版本
     */
    @Column(name = "version", length = 20)
    private String version;

    /* ==================== 以下为非数据库字段 ==================== */

    /**
     * 跳转条件
     */
    List<Skip> skipList = new ArrayList<>();

    /* ==================== 以下为 Node 接口方法实现 ==================== */

    @Override
    public Node setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Node setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Node setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Node setCreateBy(String createBy) {
        if (createBy != null) {
            this.createBy = Long.valueOf(createBy);
        } else {
            this.createBy = null;
        }

        return this;
    }

    @Override
    public Node setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updateBy = Long.valueOf(updateBy);
        } else {
            this.updateBy = null;
        }

        return this;
    }

    @Override
    public Node setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public Node setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.delFlag = Long.valueOf(delFlag);
        } else {
            this.delFlag = null;
        }

        return this;
    }
}


