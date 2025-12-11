package com.cmsr.onebase.module.engine.orm.anyline.entity;

import com.cmsr.onebase.framework.data.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
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
@Table(name = "bpm_flow_definition")
public class FlowDefinition extends BaseEntity implements Definition {
    public static final String FLOW_CODE = "flow_code";

    public static final String DEFINITION_UUID = "definition_uuid";

    public static final String IS_PUBLISH = "is_publish";

    public static final String FORM_PATH = "form_path";


    /** 流程定义UUID */
    @Column(name = "definition_uuid", length = 64, nullable = false)
    private String definitionUuid;

    /** 流程编码 */
    @Column(name = FLOW_CODE, length = 40, nullable = false)
    private String flowCode;

    /** 流程名称 */
    @Column(name = "flow_name", length = 100, nullable = false)
    private String flowName;

    /** 设计器模型（CLASSICS/MIMIC） */
    @Column(name = "model_value", length = 40, nullable = false)
    private String modelValue;

    /** 流程类别 */
    @Column(name = "category", length = 100)
    private String category;

    /** 是否发布（0未发布 1已发布 9失效） */
    @Column(name = IS_PUBLISH, nullable = false)
    private Integer isPublish;

    /** 审批表单是否自定义（Y/N） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = FORM_PATH, length = 100)
    private String formPath;

    /** 流程激活状态（0挂起 1激活） */
    @Column(name = "activity_status", nullable = false)
    private Integer activityStatus;

    /** 监听器类型 */
    @Column(name = "listener_type", length = 100)
    private String listenerType;

    /** 监听器路径 */
    @Column(name = "listener_path", length = 400)
    private String listenerPath;

    /** 业务详情（JSON） */
    @Column(name = "ext", length = 500)
    private String ext;

    /**
     * 流程版本
     */
    @Column(name = "version", length = 20, nullable = false)
    private String version;

    /**
     * 流程版本备注
     */
    @Column(name = "version_alias", length = 500)
    private String versionAlias;

    /**
     * 应用ID
     */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;


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

    public Definition setDefinitionUuid(String definitionUuid) {
        this.definitionUuid = definitionUuid;
        return this;
    }
}


