package com.cmsr.onebase.module.system.dal.dataobject.dept;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * 岗位表
 *
 * @author ma
 */
@Table(value = "system_post")
@Data
public class PostDO extends BaseEntity {

    public static final String NAME   = "name";
    public static final String CODE   = "code";
    public static final String SORT   = "sort";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";

    /**
     * 岗位名称
     */
    @Column(value = NAME)
    private String name;
    /**
     * 岗位编码
     */
    @Column(value = CODE)
    private String code;
    /**
     * 岗位排序
     */
    @Column(value = SORT)
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;
    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;

}
