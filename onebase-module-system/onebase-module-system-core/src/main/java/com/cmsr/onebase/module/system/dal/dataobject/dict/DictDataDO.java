package com.cmsr.onebase.module.system.dal.dataobject.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 字典数据表
 *
 * @author ma
 */
@Table(value = "system_dict_data")
@Data
@TenantIgnore
public class DictDataDO extends BaseEntity {

    // 字段名常量
    public static final String SORT       = "sort";
    public static final String LABEL      = "label";
    public static final String VALUE      = "value";
    public static final String DICT_TYPE  = "dict_type";
    public static final String STATUS     = "status";
    public static final String COLOR_TYPE = "color_type";
    public static final String CSS_CLASS  = "css_class";
    public static final String REMARK     = "remark";

    /**
     * 字典排序
     */
    @Column(value = SORT)
    private Integer sort;
    /**
     * 字典标签
     */
    @Column(value = LABEL)
    private String  label;
    /**
     * 字典值
     */
    @Column(value = VALUE)
    private String  value;
    /**
     * 字典类型
     * <p>
     * 冗余 {@link DictDataDO#getDictType()}
     */
    @Column(value = DICT_TYPE)
    private String  dictType;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;
    /**
     * 颜色类型
     * <p>
     * 对应到 element-ui 为 default、primary、success、info、warning、danger
     */
    @Column(value = COLOR_TYPE)
    private String  colorType;
    /**
     * css 样式
     */
    @Column(value = CSS_CLASS)
    private String  cssClass;
    /**
     * 备注
     */
    @Column(value = REMARK)
    private String  remark;

}
