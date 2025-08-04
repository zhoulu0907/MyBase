package com.cmsr.onebase.module.system.dal.dataobject.tenant;

import java.util.Set;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 租户套餐 DO
 *
 */
@Table(name = "system_tenant_package")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantPackageDO extends BaseDO {

    // 字段列名常量
    public static final String NAME     = "name";
    public static final String STATUS   = "status";
    public static final String REMARK   = "remark";
    public static final String MENU_IDS = "menu_ids";

    /**
     * 套餐名，唯一
     */
    @Column(name = NAME)
    private String name;
    /**
     * 租户套餐状态
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
    /**
     * 关联的菜单编号
     */
    @Column(name = MENU_IDS)
    private Set<Long> menuIds;

}
