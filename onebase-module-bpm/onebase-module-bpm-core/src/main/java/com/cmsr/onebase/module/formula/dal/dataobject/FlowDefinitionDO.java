package com.cmsr.onebase.module.formula.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName FlowDefinitionDO
 * @Description 流程定义表 DO
 * @Author bty418
 * @Date 2025/08/06 14:00
 */
@Table(name = "flow_definition")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinitionDO extends TenantBaseDO {

    // 列名常量
    public static final String FLOW_CODE = "flow_code";
    public static final String FLOW_NAME = "flow_name";
    public static final String MODEL_VALUE = "model_value";
    public static final String CATEGORY = "category";
    public static final String VERSION = "version";
    public static final String IS_PUBLISH = "is_publish";
    public static final String FORM_CUSTOM = "form_custom";
    public static final String FORM_PATH = "form_path";
    public static final String ACTIVITY_STATUS = "activity_status";
    public static final String LISTENER_TYPE = "listener_type";
    public static final String LISTENER_PATH = "listener_path";
    public static final String EXT = "ext";

    /**
     * 流程编码
     */
    @Column(name = FLOW_CODE)
    private String flowCode;

    /**
     * 流程名称
     */
    @Column(name = FLOW_NAME)
    private String flowName;

    /**
     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
     */
    @Column(name = MODEL_VALUE)
    private String modelValue;

    /**
     * 流程类别
     */
    @Column(name = CATEGORY)
    private String category;

    /**
     * 流程版本
     */
    @Column(name = VERSION)
    private String version;

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    @Column(name = IS_PUBLISH)
    private Integer isPublish;

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    @Column(name = FORM_CUSTOM)
    private String formCustom;

    /**
     * 审批表单路径
     */
    @Column(name = FORM_PATH)
    private String formPath;

    /**
     * 流程激活状态（0挂起 1激活）
     */
    @Column(name = ACTIVITY_STATUS)
    private Integer activityStatus;

    /**
     * 监听器类型
     */
    @Column(name = LISTENER_TYPE)
    private String listenerType;

    /**
     * 监听器路径
     */
    @Column(name = LISTENER_PATH)
    private String listenerPath;

    /**
     * 扩展字段，预留给业务系统使用
     */
    @Column(name = EXT)
    private String ext;


}