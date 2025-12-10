package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WarmFlow 流程节点 DO，对应表 bpm_flow_node。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_node")
public class FlowNode extends WarmFlowBizEntity implements Node {
    /** 节点类型（0开始 1中间 2结束 3互斥网关 4并行网关） */
    @Column(value = "node_type", comment = "节点类型（0开始 1中间 2结束 3互斥网关 4并行网关）")
    private Integer nodeType;

    /** 流程定义ID */
    @Column(value = "definition_id", comment = "流程定义ID")
    private Long definitionId;

    /** 流程定义UUID（备用） */
    @Column(value = "definition_uuid", comment = "流程定义UUID")
    private String definitionUuid;

    /** 节点编码 */
    @Column(value = "node_code", comment = "节点编码")
    private String nodeCode;

    /** 节点名称 */
    @Column(value = "node_name", comment = "节点名称")
    private String nodeName;

    /** 权限标识（如 @@role:2） */
    @Column(value = "permission_flag", comment = "权限标识（如 @@role:2）")
    private String permissionFlag;

    /** 签署比例值 */
    @Column(value = "node_ratio", comment = "签署比例值")
    private BigDecimal nodeRatio;

    /** 坐标 */
    @Column(value = "coordinate", comment = "坐标")
    private String coordinate;

    /** 任意节点跳转 */
    @Column(value = "any_node_skip", comment = "任意节点跳转")
    private String anyNodeSkip;

    /** 监听器类型 */
    @Column(value = "listener_type", comment = "监听器类型")
    private String listenerType;

    /** 监听器路径 */
    @Column(value = "listener_path", comment = "监听器路径")
    private String listenerPath;

    /** 处理器类型 */
    @Column(value = "handler_type", comment = "处理器类型")
    private String handlerType;

    /** 处理器路径 */
    @Column(value = "handler_path", comment = "处理器路径")
    private String handlerPath;

    /** 审批表单是否自定义（Y/N） */
    @Column(value = "form_custom", comment = "审批表单是否自定义（Y/N）")
    private String formCustom;

    /** 审批表单路径 */
    @Column(value = "form_path", comment = "审批表单路径")
    private String formPath;

    /** 扩展属性 */
    @Column(value = "ext", comment = "扩展属性")
    private String ext;

    /**
     * 版本
     */
    @Column(value = "bpm_version", comment = "版本")
    private String bpmVersion;

    /* ==================== 以下为非数据库字段 ==================== */

    /**
     * 跳转条件
     */
    @Column(ignore = true)
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
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public Node setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public Node setTenantId(String tenantId) {
        if (tenantId != null) {
            this.wfTenantId = Long.valueOf(tenantId);
        } else {
            this.wfTenantId = null;
        }

        return this;
    }

    @Override
    public Node setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }

    @Override
    public Node setVersion(String bpmVersion) {
        this.bpmVersion = bpmVersion;
        return this;
    }

    @Override
    public String getVersion() {
        return this.bpmVersion;
    }

    public Node setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public Node setVersionTag(Long versionTag) {
        this.versionTag = versionTag;
        return this;
    }

    public Node setDefinitionUuid(String definitionUuid) {
        this.definitionUuid = definitionUuid;
        return this;
    }
}


