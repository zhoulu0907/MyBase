package com.cmsr.onebase.module.system.dal.dataobject.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 空间 DO
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
@Table(value = "system_tenant")
public class TenantDO extends BaseEntity {

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
     * 空间名，唯一
     */
    @Column(value = NAME)
    private String name;
    /**
     * 联系人的用户编号
     * 关联 {@link com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO#getId()}
     */
    @Column(value = ADMIN_USER_ID)
    private Long adminUserId;
    /**
     * 空间状态
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;
    /**
     * 绑定域名
     */
    @Column(value = WEBSITE)
    private String website;
    /**
     * 绑定域名
     */
    @Column(value = WEBSITE_H5)
    private String websiteH5;
    /**
     * 租户套餐编号
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    @Column(value = PACKAGE_ID)
    private Long packageId;
    /**
     * 过期时间
     */
    @Column(value = EXPIRE_TIME)
    private LocalDateTime expireTime;
    /**
     * 账号数量上限
     */
    @Column(value = ACCOUNT_COUNT)
    private Integer accountCount;

    /**
     * 租户编码 - 系统
     */
    @Column(value = TENANT_CODE)
    private String tenantCode;


    /**
     * key
     */
    @Column(value = TENANT_KEY)
    private String tenantkey;

    /**
     * secret
     */
    @Column(value = TENANT_SECRET)
    private String tenantSecret;


    /**
     * 访问地址
     */
    @Column(value = "ACCESS_URL")
    private String accessUrl;

    /**
     * saas功能是否开启默认0，开启1
     */
    @Column(value = "PUBLISH_MODEL")
    private String publishModel;

    /**
     * 用户logo
     */
    @Column(value = "LOGO_URL")
    private String logoUrl;

}
