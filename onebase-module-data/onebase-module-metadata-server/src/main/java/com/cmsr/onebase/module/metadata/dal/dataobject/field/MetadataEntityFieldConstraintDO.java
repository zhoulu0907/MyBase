package com.cmsr.onebase.module.metadata.dal.dataobject.field;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 实体字段-约束定义（长度范围/正则校验） DO
 *
 * <p>对应表：metadata_entity_field_constraint</p>
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_entity_field_constraint")
public class MetadataEntityFieldConstraintDO extends TenantBaseDO {

    // 列名常量
    public static final String FIELD_ID = "field_id";
    public static final String CONSTRAINT_TYPE = "constraint_type";
    public static final String MIN_LENGTH = "min_length";
    public static final String MAX_LENGTH = "max_length";
    public static final String REGEX_PATTERN = "regex_pattern";
    public static final String PROMPT_MESSAGE = "prompt_message";
    public static final String IS_ENABLED = "is_enabled";
    public static final String RUN_MODE = "run_mode";
    public static final String APP_ID = "app_id";

    /**
     * 关联字段ID（metadata_entity_field.id）
     */
    @Column(name = FIELD_ID)
    private Long fieldId;

    /**
     * 约束类型：LENGTH_RANGE/REGEX
     */
    @Column(name = CONSTRAINT_TYPE)
    private String constraintType;

    /**
     * 最小长度（LENGTH_RANGE有效）
     */
    @Column(name = MIN_LENGTH)
    private Integer minLength;

    /**
     * 最大长度（LENGTH_RANGE有效）
     */
    @Column(name = MAX_LENGTH)
    private Integer maxLength;

    /**
     * 正则表达式（REGEX有效）
     */
    @Column(name = REGEX_PATTERN)
    private String regexPattern;

    /**
     * 提示信息
     */
    @Column(name = PROMPT_MESSAGE)
    private String promptMessage;

    /**
     * 是否启用：0-是，1-否
     */
    @Column(name = IS_ENABLED)
    private Integer isEnabled;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    @Column(name = RUN_MODE)
    private Integer runMode;

    /**
     * 应用ID
     */
    @Column(name = APP_ID)
    private Long appId;
}


