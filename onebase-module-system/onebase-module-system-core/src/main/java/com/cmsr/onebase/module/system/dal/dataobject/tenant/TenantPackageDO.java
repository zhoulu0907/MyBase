package com.cmsr.onebase.module.system.dal.dataobject.tenant;

import java.util.Set;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 租户套餐 DO
 *
 */
@Table(value = "system_tenant_package")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantPackageDO extends BaseEntity {

    // 字段列名常量
    public static final String NAME     = "name";
    public static final String STATUS   = "status";
    public static final String REMARK   = "remark";
    public static final String MENU_IDS = "menu_ids";
    public static final String CODE = "code";

    /**
     * 套餐名，唯一
     */
    @Column(value = NAME)
    private String name;
    /**
     * 租户套餐状态
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

    /**
     * 租户套餐编码
     */
    @Column(value = CODE)
    private String code;

    /**
     * 关联的菜单编号
     */
    @Column(value = MENU_IDS)
    private Set<Long> menuIds;

}
