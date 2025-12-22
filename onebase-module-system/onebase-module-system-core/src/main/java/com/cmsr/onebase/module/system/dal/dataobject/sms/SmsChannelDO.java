package com.cmsr.onebase.module.system.dal.dataobject.sms;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.framework.sms.core.enums.SmsChannelEnum;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.ToString;

/**
 * 短信渠道 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(value = "system_sms_channel")
@Data
@ToString(callSuper = true)
@TenantIgnore
public class SmsChannelDO extends BaseEntity {

    // 字段列名常量
    public static final String SIGNATURE    = "signature";
    public static final String CODE         = "code";
    public static final String STATUS       = "status";
    public static final String REMARK       = "remark";
    public static final String API_KEY      = "api_key";
    public static final String API_SECRET   = "api_secret";
    public static final String CALLBACK_URL = "callback_url";

    /**
     * 短信签名
     */
    @Column(value = SIGNATURE)
    private String signature;
    /**
     * 渠道编码
     *
     * 枚举 {@link SmsChannelEnum}
     */
    @Column(value = CODE)
    private String code;
    /**
     * 启用状态
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
     * 短信 API 的账号
     */
    @Column(value = API_KEY)
    private String apiKey;
    /**
     * 短信 API 的密钥
     */
    @Column(value = API_SECRET)
    private String apiSecret;
    /**
     * 短信发送回调 URL
     */
    @Column(value = CALLBACK_URL)
    private String callbackUrl;
}
