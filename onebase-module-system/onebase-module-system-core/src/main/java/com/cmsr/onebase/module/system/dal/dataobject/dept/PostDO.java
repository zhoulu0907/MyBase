package com.cmsr.onebase.module.system.dal.dataobject.dept;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import lombok.Data;

/**
 * 岗位表
 *
 * @author ma
 */
@Table(name = "system_post")
@Data
public class PostDO extends BaseDO {

    public static final String NAME   = "name";
    public static final String CODE   = "code";
    public static final String SORT   = "sort";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";

    /**
     * 岗位名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 岗位编码
     */
    @Column(name = CODE)
    private String code;
    /**
     * 岗位排序
     */
    @Column(name = SORT)
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

}
