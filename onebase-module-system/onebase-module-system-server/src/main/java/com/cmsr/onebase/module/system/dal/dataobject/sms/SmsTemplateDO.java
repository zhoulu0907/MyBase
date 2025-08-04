package com.cmsr.onebase.module.system.dal.dataobject.sms;

import java.util.List;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.enums.sms.SmsTemplateTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 短信模板 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(name = "system_sms_template")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class SmsTemplateDO extends BaseDO {

    // 字段列名常量
    public static final String TYPE            = "type";
    public static final String STATUS          = "status";
    public static final String CODE            = "code";
    public static final String NAME            = "name";
    public static final String CONTENT         = "content";
    public static final String PARAMS          = "params";
    public static final String REMARK          = "remark";
    public static final String API_TEMPLATE_ID = "api_template_id";
    public static final String CHANNEL_ID      = "channel_id";
    public static final String CHANNEL_CODE    = "channel_code";

    // ========= 模板相关字段 =========

    /**
     * 短信类型
     *
     * 枚举 {@link SmsTemplateTypeEnum}
     */
    @Column(name = TYPE)
    private Integer type;
    /**
     * 启用状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 模板编码，保证唯一
     */
    @Column(name = CODE)
    private String code;
    /**
     * 模板名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 模板内容
     *
     * 内容的参数，使用 {} 包括，例如说 {name}
     */
    @Column(name = CONTENT)
    private String content;
    /**
     * 参数数组(自动根据内容生成)
     */
    @Column(name = PARAMS)
    private List<String> params;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;
    /**
     * 短信 API 的模板编号
     */
    @Column(name = API_TEMPLATE_ID)
    private String apiTemplateId;

    // ========= 渠道相关字段 =========

    /**
     * 短信渠道编号
     *
     * 关联 {@link SmsChannelDO#getId()}
     */
    @Column(name = CHANNEL_ID)
    private Long channelId;
    /**
     * 短信渠道编码
     *
     * 冗余 {@link SmsChannelDO#getCode()}
     */
    @Column(name = CHANNEL_CODE)
    private String channelCode;

}
