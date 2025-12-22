package com.cmsr.onebase.module.system.dal.dataobject.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 字典类型表
 *
 * @author ma
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@Table(value = "system_dict_type")
public class DictTypeDO extends BaseDO {

    // 字段名常量
    public static final String NAME            = "name";
    public static final String TYPE            = "type";
    public static final String STATUS          = "status";
    public static final String REMARK          = "remark";
    public static final String DICT_OWNER_TYPE = "dict_owner_type";
    public static final String DICT_OWNER_ID   = "dict_owner_id";
    public static final String DELETED_TIME    = "deleted_time";

    /**
     * 字典名称
     */
    @Column(value = NAME)
    private String name;

    /**
     * 字典类型
     */
    @Column(value = TYPE)
    private String type;

    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;

    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;

    /**
     * 字典所有者类型
     * <p>
     * 枚举 {@link com.cmsr.onebase.module.system.enums.dict.DictOwnerTypeEnum}
     */
    @Column(value = DICT_OWNER_TYPE)
    private String dictOwnerType;

    /**
     * 字典所有者ID
     * <p>
     * 当dictOwnerType为app时，存储appId；为tenant时，存储tenantId
     */
    @Column(value = DICT_OWNER_ID)
    private Long dictOwnerId;

    /**
     * 删除时间
     */
    @Column(value = DELETED_TIME)
    private LocalDateTime deletedTime;

}
