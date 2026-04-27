package com.cmsr.onebase.module.infra.dal.dataobject.security;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 安全配置模板表
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_security_config_template")
@TenantIgnore
public class SecurityConfigTemplateDO extends BaseDO {

    public static final String CATEGORY_ID = "category_id";
    public static final String CONFIG_KEY = "config_key";
    public static final String CONFIG_NAME = "config_name";
    public static final String DATA_TYPE = "data_type";
    public static final String DEFAULT_VALUE = "default_value";
    public static final String DESCRIPTION = "description";
    public static final String SORT_ORDER = "sort_order";
    public static final String OPTIONS = "options";
    public static final String MAXVALUE = "maxvalue";
    public static final String MINVALUE = "minvalue";
    public static final String REQUIRED = "required";
    public static final String WIDGET_TYPE = "widget_type";

    /**
     * 分类ID
     */
    @Column(name = CATEGORY_ID)
    private Long categoryId;

    /**
     * 配置键
     */
    @Column(name = CONFIG_KEY)
    private String configKey;

    /**
     * 配置名称
     */
    @Column(name = CONFIG_NAME)
    private String configName;

    /**
     * 数据类型
     */
    @Column(name = DATA_TYPE)
    private String dataType;

    /**
     * 模板默认值（来自模板表infra_security_config_template.default_value）
     */
    @Column(name = DEFAULT_VALUE)
    private String defaultValue;

    /**
     * 有效配置值（租户配置值优先，若无则回落到模板默认值）。非持久化字段，仅用于查询结果承载。
     */
    @Transient
    private String configValue;

    @Column(name = OPTIONS)
    private String options;

    @Column(name = MAXVALUE)
    private Long maxValue;

    @Column(name = MINVALUE)
    private Long minValue;

    @Column(name = REQUIRED)
    private String required;

    /**
     * 界面组件类型
     */
    @Column(name = WIDGET_TYPE)
    private String widgetType;
    /**
     * 描述
     */
    @Column(name = DESCRIPTION)
    private String description;

    /**
     * 排序号
     */
    @Column(name = SORT_ORDER)
    private Integer sortOrder;

}
