package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WarmFlow 流程定义 DO，对应表 flow_definition。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_definition")
public class FlowDefinition extends WarmFlowBaseEntity implements Definition {
    /** 流程编码 */
    @Column(value = "flow_code", comment = "流程编码")
    private String flowCode;

    /** 流程名称 */
    @Column(value = "flow_name", comment = "流程名称")
    private String flowName;

    /** 设计器模型（CLASSICS/MIMIC） */
    @Column(value = "model_value", comment = "设计器模型（CLASSICS/MIMIC）")
    private String modelValue;

    /** 流程类别 */
    @Column(value = "category", comment = "流程类别")
    private String category;

    /** 是否发布（0未发布 1已发布 9失效） */
    @Column(value = "is_publish", comment = "是否发布（0未发布 1已发布 9失效）")
    private Integer isPublish;

    /** 审批表单是否自定义（Y/N） */
    @Column(value = "form_custom", comment = "审批表单是否自定义（Y/N）")
    private String formCustom;

    /** 审批表单路径 */
    @Column(value = "form_path", comment = "审批表单路径")
    private String formPath;

    /** 流程激活状态（0挂起 1激活） */
    @Column(value = "activity_status", comment = "流程激活状态（0挂起 1激活）")
    private Integer activityStatus;

    /** 监听器类型 */
    @Column(value = "listener_type", comment = "监听器类型")
    private String listenerType;

    /** 监听器路径 */
    @Column(value = "listener_path", comment = "监听器路径")
    private String listenerPath;

    /** 业务详情（JSON） */
    @Column(value = "ext", comment = "业务详情（JSON）")
    private String ext;

    /**
     * 流程版本
     */
    @Column(value = "version", comment = "流程版本")
    private String version;


    /* ==================== 以下为非数据库字段 ==================== */

    private List<Node> nodeList = new ArrayList<>();

    private List<User> userList = new ArrayList<>();


    /* ==================== 以下为 Definition 接口方法实现 ==================== */

    @Override
    public Definition setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Definition setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Definition setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Definition setCreateBy(String createBy) {
        if (createBy != null) {
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public Definition setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public Definition setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public Definition setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}


