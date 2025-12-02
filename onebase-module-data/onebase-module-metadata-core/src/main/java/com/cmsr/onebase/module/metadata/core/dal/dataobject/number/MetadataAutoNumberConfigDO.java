package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动编号-字段配置 DO
 *
 * 对应表：metadata_auto_number_config
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_auto_number_config")
public class MetadataAutoNumberConfigDO extends BaseTenantEntity {

    /**
     * 字段ID
     */
    @Column(value = "field_id", comment = "字段ID")
    private Long fieldId;

    /**
     * 编号模式
     */
    @Column(value = "number_mode", comment = "编号模式")
    private String numberMode;

    /**
     * 数字宽度
     */
    @Column(value = "digit_width", comment = "数字宽度")
    private Short digitWidth;

    /**
     * 溢出后是否继续
     */
    @Column(value = "overflow_continue", comment = "溢出后是否继续")
    private Integer overflowContinue;

    /**
     * 初始值
     */
    @Column(value = "initial_value", comment = "初始值")
    private Long initialValue;

    /**
     * 重置周期
     */
    @Column(value = "reset_cycle", comment = "重置周期")
    private String resetCycle;

    /**
     * 下一条记录以修改后的开始值编号：1-是，0-否
     */
    @Column(value = "reset_on_initial_change", comment = "下一条记录以修改后的开始值编号：1-是，0-否")
    private Integer resetOnInitialChange;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    /**
     * 版本标识
     */
    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;
}


