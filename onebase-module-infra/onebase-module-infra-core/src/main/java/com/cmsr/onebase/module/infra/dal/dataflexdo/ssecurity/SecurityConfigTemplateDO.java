package com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全配置模板表
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_security_config_template")
@TenantIgnore
public class SecurityConfigTemplateDO extends BaseEntity {

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
    @Column(value = CATEGORY_ID)
    private Long categoryId;

    /**
     * 配置键
     */
    @Column(value = CONFIG_KEY)
    private String configKey;

    /**
     * 配置名称
     */
    @Column(value = CONFIG_NAME)
    private String configName;

    /**
     * 数据类型
     */
    @Column(value = DATA_TYPE)
    private String dataType;

    /**
     * 模板默认值（来自模板表infra_security_config_template.default_value）
     */
    @Column(value = DEFAULT_VALUE)
    private String defaultValue;

    /**
     * 有效配置值（租户配置值优先，若无则回落到模板默认值）。非持久化字段，仅用于查询结果承载。
     */
    @Transient
    private String configValue;

    @Column(value = OPTIONS)
    private String options;

    @Column(value = MAXVALUE)
    private Long maxValue;

    @Column(value = MINVALUE)
    private Long minValue;

    @Column(value = REQUIRED)
    private String required;

    /**
     * 界面组件类型
     */
    @Column(value = WIDGET_TYPE)
    private String widgetType;
    /**
     * 描述
     */
    @Column(value = DESCRIPTION)
    private String description;

    /**
     * 排序号
     */
    @Column(value = SORT_ORDER)
    private Integer sortOrder;

}
