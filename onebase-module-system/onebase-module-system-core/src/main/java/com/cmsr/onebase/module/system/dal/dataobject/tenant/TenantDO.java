package com.cmsr.onebase.module.system.dal.dataobject.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 租户 DO
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
@Table(name = "system_tenant")
public class TenantDO extends BaseDO {

    // 字段列名常量
    public static final String NAME = "name";
    public static final String ADMIN_USER_ID = "admin_user_id";
    public static final String STATUS = "status";
    public static final String WEBSITE = "website";
    public static final String WEBSITE_H5 = "website_h5";
    public static final String PACKAGE_ID = "package_id";
    public static final String EXPIRE_TIME = "expire_time";
    public static final String ACCOUNT_COUNT = "account_count";
    public static final String TENANT_CODE = "tenant_code";
    public static final String TENANT_KEY = "tenant_key";
    public static final String TENANT_SECRET = "tenant_secret";
    // 在其他字段常量后添加
    public static final String ACCESS_URL = "access_url";
    public static final String PUBLISH_MODEL = "publish_model";
    public static final String LOGO_URL = "logo_url";

    // builder模式可正常运作
    public TenantDO setId(Long id) {
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
     * 关联 {@link com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO#getId()}
     */
    @Column(name = ADMIN_USER_ID)
    private Long adminUserId;
    /**
     * 租户状态
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
     * 绑定域名
     */
    @Column(name = WEBSITE_H5)
    private String websiteH5;
    /**
     * 租户套餐编号
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
     * 账号数量上限
     */
    @Column(name = ACCOUNT_COUNT)
    private Integer accountCount;

    /**
     * 租户编码 - 系统
     */
    @Column(name = TENANT_CODE)
    private String tenantCode;


    /**
     * key
     */
    @Column(name = TENANT_KEY)
    private String tenantkey;

    /**
     * secret
     */
    @Column(name = TENANT_SECRET)
    private String tenantSecret;


    /**
     * 访问地址
     */
    @Column(name = "ACCESS_URL")
    private String accessUrl;

    /**
     * saas功能是否开启默认0，开启1
     */
    @Column(name = "PUBLISH_MODEL")
    private String publishModel;

    /**
     * 用户logo
     */
    @Column(name = "LOGO_URL")
    private String logoUrl;

}
