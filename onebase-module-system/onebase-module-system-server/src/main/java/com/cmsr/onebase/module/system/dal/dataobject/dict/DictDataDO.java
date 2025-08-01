package com.cmsr.onebase.module.system.dal.dataobject.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import jakarta.persistence.Table;
import jakarta.persistence.Column;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 字典数据表
 *
 * @author ruoyi
 */
@Table(name="system_dict_data")
@Data
@TenantIgnore
public class DictDataDO extends BaseDO {

    // 字段名常量
    public static final String FIELD_SORT       = "sort";
    public static final String FIELD_LABEL      = "label";
    public static final String FIELD_VALUE      = "value";
    public static final String FIELD_DICT_TYPE  = "dict_type";
    public static final String FIELD_STATUS     = "status";
    public static final String FIELD_COLOR_TYPE = "color_type";
    public static final String FIELD_CSS_CLASS  = "css_class";
    public static final String FIELD_REMARK     = "remark";

    /**
     * 字典排序
     */
    @Column(name = FIELD_SORT)
    private Integer sort;
    /**
     * 字典标签
     */
    @Column(name = FIELD_LABEL)
    private String label;
    /**
     * 字典值
     */
    @Column(name = FIELD_VALUE)
    private String value;
    /**
     * 字典类型
     *
     * 冗余 {@link DictDataDO#getDictType()}
     */
    @Column(name = FIELD_DICT_TYPE)
    private String dictType;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = FIELD_STATUS)
    private Integer status;
    /**
     * 颜色类型
     *
     * 对应到 element-ui 为 default、primary、success、info、warning、danger
     */
    @Column(name = FIELD_COLOR_TYPE)
    private String colorType;
    /**
     * css 样式
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    @Column(name = FIELD_CSS_CLASS)
    private String cssClass;
    /**
     * 备注
     */
    @Column(name = FIELD_REMARK)
    private String remark;

}
