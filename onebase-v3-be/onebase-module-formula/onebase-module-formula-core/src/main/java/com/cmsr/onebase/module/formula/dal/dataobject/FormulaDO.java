package com.cmsr.onebase.module.formula.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 公式数据对象
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Data
@Table(name = FormulaDO.TABLE_NAME)
public class FormulaDO extends TenantBaseDO {

    public static final String TABLE_NAME = "formula_formula";

    // 字段名常量
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_EXPRESSION = "expression";
    public static final String FIELD_USE_SCENE = "use_scene";
    public static final String FIELD_TYPE = "type";

    /**
     * 公式名称
     */
    @Column(name = FIELD_NAME, columnDefinition = "VARCHAR(128) NOT NULL", nullable = false, length = 128, comment = "公式名称")
    private String name;

    /**
     * 公式描述
     */
    @Column(name = FIELD_DESCRIPTION, columnDefinition = "VARCHAR(1024)", length = 1024, comment = "公式描述")
    private String description;

    /**
     * 公式表达式
     */
    @Column(name = FIELD_EXPRESSION, columnDefinition = "TEXT NOT NULL", nullable = false, comment = "公式表达式")
    private String expression;

    /**
     * 使用场景（枚举code）
     */
    @Column(name = FIELD_USE_SCENE, columnDefinition = "VARCHAR(64) NOT NULL", nullable = false, length = 64, comment = "使用场景")
    private String useScene;

    /**
     * 公式类型（内置/自定义）
     */
    @Column(name = FIELD_TYPE, columnDefinition = "VARCHAR(32) NOT NULL", nullable = false, length = 32, comment = "公式类型")
    private String type;

}
