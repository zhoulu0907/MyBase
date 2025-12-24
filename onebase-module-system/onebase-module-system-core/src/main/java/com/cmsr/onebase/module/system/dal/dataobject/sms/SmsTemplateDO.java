package com.cmsr.onebase.module.system.dal.dataobject.sms;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.flex.typehandler.ListStringTypeHandler;
import com.cmsr.onebase.module.system.enums.sms.SmsTemplateTypeEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.List;

/**
 * 短信模板 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(value = "system_sms_template")
@Data
@TenantIgnore
public class SmsTemplateDO extends BaseEntity {

    // 字段列名常量
    public static final String TYPE = "type";
    public static final String STATUS = "status";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    public static final String PARAMS = "params";
    public static final String REMARK = "remark";
    public static final String API_TEMPLATE_ID = "api_template_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_CODE = "channel_code";

    // ========= 模板相关字段 =========

    /**
     * 短信类型
     * <p>
     * 枚举 {@link SmsTemplateTypeEnum}
     */
    @Column(value = TYPE)
    private Integer type;
    /**
     * 启用状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;
    /**
     * 模板编码，保证唯一
     */
    @Column(value = CODE)
    private String code;
    /**
     * 模板名称
     */
    @Column(value = NAME)
    private String name;
    /**
     * 模板内容
     * <p>
     * 内容的参数，使用 {} 包括，例如说 {name}
     */
    @Column(value = CONTENT)
    private String content;
    /**
     * 参数数组(自动根据内容生成)
     */
    @Column(value = PARAMS, typeHandler = ListStringTypeHandler.class)
    private List<String> params;
    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;
    /**
     * 短信 API 的模板编号
     */
    @Column(value = API_TEMPLATE_ID)
    private String apiTemplateId;

    // ========= 渠道相关字段 =========

    /**
     * 短信渠道编号
     * <p>
     * 关联 {@link SmsChannelDO#getId()}
     */
    @Column(value = CHANNEL_ID)
    private Long channelId;
    /**
     * 短信渠道编码
     * <p>
     * 冗余 {@link SmsChannelDO#getCode()}
     */
    @Column(value = CHANNEL_CODE)
    private String channelCode;

}
