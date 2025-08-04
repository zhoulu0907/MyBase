package com.cmsr.onebase.module.system.dal.dataobject.sms;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.framework.sms.core.enums.SmsChannelEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * 短信渠道 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(name = "system_sms_channel")
@Data
@ToString(callSuper = true)
@TenantIgnore
public class SmsChannelDO extends BaseDO {

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
    @Column(name = SIGNATURE)
    private String signature;
    /**
     * 渠道编码
     *
     * 枚举 {@link SmsChannelEnum}
     */
    @Column(name = CODE)
    private String code;
    /**
     * 启用状态
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
     * 短信 API 的账号
     */
    @Column(name = API_KEY)
    private String apiKey;
    /**
     * 短信 API 的密钥
     */
    @Column(name = API_SECRET)
    private String apiSecret;
    /**
     * 短信发送回调 URL
     */
    @Column(name = CALLBACK_URL)
    private String callbackUrl;
}
