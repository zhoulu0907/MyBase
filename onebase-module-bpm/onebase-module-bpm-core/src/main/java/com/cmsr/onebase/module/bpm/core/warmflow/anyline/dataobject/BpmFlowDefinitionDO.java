package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;

import java.util.ArrayList;
import java.util.Date;
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
public class BpmFlowDefinitionDO extends BpmWarmFlowBaseDO<Definition> implements Definition {

    /** 流程编码 */
    @Column(name = "flow_code", length = 40, nullable = false)
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
    @Column(name = "is_publish", nullable = false)
    private Integer isPublish;

    /** 审批表单是否自定义（Y/N） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
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

    /* ==================== 以下为非数据库字段 ==================== */

    private List<Node> nodeList = new ArrayList<>();

    private List<User> userList = new ArrayList<>();
}


