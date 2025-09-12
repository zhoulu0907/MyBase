package com.cmsr.onebase.module.system.dal.dataobject.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.Data;

/**
 * 字典数据表
 *
 * @author ma
 */
@Table(name="system_dict_data")
@Data
@TenantIgnore
public class DictDataDO extends BaseDO {

    // 字段名常量
    public static final String SORT = "sort";
    public static final String LABEL = "label";
    public static final String VALUE = "value";
    public static final String DICT_TYPE = "dict_type";
    public static final String STATUS = "status";
    public static final String COLOR_TYPE = "color_type";
    public static final String CSS_CLASS = "css_class";
    public static final String REMARK = "remark";

    /**
     * 字典排序
     */
    @Column(name = SORT)
    private Integer sort;
    /**
     * 字典标签
     */
    @Column(name = LABEL)
    private String label;
    /**
     * 字典值
     */
    @Column(name = VALUE)
    private String value;
    /**
     * 字典类型
     *
     * 冗余 {@link DictDataDO#getDictType()}
     */
    @Column(name = DICT_TYPE)
    private String dictType;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 颜色类型
     *
     * 对应到 element-ui 为 default、primary、success、info、warning、danger
     */
    @Column(name = COLOR_TYPE)
    private String colorType;
    /**
     * css 样式
     */
    @Column(name = CSS_CLASS)
    private String cssClass;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

}
