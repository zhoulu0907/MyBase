package com.cmsr.onebase.module.infra.dal.dataflexdo.config;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.enums.config.ConfigTypeEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数配置表
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_config")
@TenantIgnore
public class ConfigDO extends BaseTenantEntity {

    // 新增各字段对应的常量
    public static final String CATEGORY = "category";
    public static final String NAME = "name";
    public static final String CONFIG_KEY = "config_key";
    public static final String VALUE = "value";
    public static final String TYPE = "type";
    public static final String VISIBLE = "visible";
    public static final String REMARK = "remark";

    /**
     * 参数分类
     */
    @Column(value = CATEGORY)
    private String category;
    /**
     * 参数名称
     */
    @Column(value = NAME)
    private String name;
    /**
     * 参数键名
     *
     * 支持多 DB 类型时，无法直接使用 key + @TableField("config_key") 来实现转换，原因是 "config_key" AS key 而存在报错
     */
    @Column(value = CONFIG_KEY)
    private String configKey;
    /**
     * 参数键值
     */
    @Column(value = VALUE)
    private String value;
    /**
     * 参数类型
     *
     * 枚举 {@link ConfigTypeEnum}
     */
    @Column(value = TYPE)
    private Integer type;
    /**
     * 是否可见
     *
     * 不可见的参数，一般是敏感参数，前端不可获取
     */
    @Column(value = VISIBLE)
    private Integer visible;
    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;

}