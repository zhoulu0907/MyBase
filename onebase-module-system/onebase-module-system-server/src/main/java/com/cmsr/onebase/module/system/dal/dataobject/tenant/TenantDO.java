package com.cmsr.onebase.module.system.dal.dataobject.tenant;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 租户 DO
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
//@TableName(value = "system_tenant", autoResultMap = true)
//@KeySequence("system_tenant_seq")
@Table(name = "system_tenant")
public class TenantDO extends BaseDO {

    // 字段列名常量
    private static final String NAME                    = "name";
    private static final String CONTACT_USER_ID         = "contact_user_id";
    private static final String CONTACT_NAME            = "contact_name";
    private static final String CONTACT_MOBILE          = "contact_mobile";
    private static final String STATUS                  = "status";
    private static final String WEBSITE                 = "website";
    private static final String PACKAGE_ID              = "package_id";
    private static final String EXPIRE_TIME             = "expire_time";
    private static final String ACCOUNT_COUNT           = "account_count";
    private static final String ALLOCATE_PERSON_COUNT   = "allocate_person_count";

    // builder模式可正常运作
    public TenantDO setId(Long id){
        super.setId(id);
        return this;
    }

    /**
     * 套餐编号 - 系统
     */
    public static final Long PACKAGE_ID_SYSTEM = 0L;

    /**
     * 租户名，唯一
     */
    @Column(name = NAME)
    private String name;
    /**
     * 联系人的用户编号
     *
     * 关联 {@link com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO#getId()}
     */
    @Column(name = CONTACT_USER_ID)
    private Long contactUserId;
    /**
     * 联系人
     */
    @Column(name = CONTACT_NAME)
    private String contactName;
    /**
     * 联系手机
     */
    @Column(name = CONTACT_MOBILE)
    private String contactMobile;
    /**
     * 租户状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 绑定域名
     */
    @Column(name = WEBSITE)
    private String website;
    /**
     * 租户套餐编号
     *
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    @Column(name = PACKAGE_ID)
    private Long packageId;
    /**
     * 过期时间
     */
    @Column(name = EXPIRE_TIME)
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    @Column(name = ACCOUNT_COUNT)
    private Integer accountCount;

    /**
     * 分配人员数量
     */
    @Column(name = ALLOCATE_PERSON_COUNT)
    private Integer allocatePersonCount;

}
