package com.cmsr.onebase.module.system.dal.dataobject.sms;

import java.time.LocalDateTime;
import java.util.Map;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 短信日志 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(value = "system_sms_log")
@Data
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class SmsLogDO extends BaseDO {

    // 字段列名常量
    public static final String CHANNEL_ID         = "channel_id";
    public static final String CHANNEL_CODE       = "channel_code";
    public static final String TEMPLATE_ID        = "template_id";
    public static final String TEMPLATE_CODE      = "template_code";
    public static final String TEMPLATE_TYPE      = "template_type";
    public static final String TEMPLATE_CONTENT   = "template_content";
    public static final String TEMPLATE_PARAMS    = "template_params";
    public static final String API_TEMPLATE_ID    = "api_template_id";
    public static final String MOBILE             = "mobile";
    public static final String USER_ID            = "user_id";
    public static final String USER_TYPE          = "user_type";
    public static final String SEND_STATUS        = "send_status";
    public static final String SEND_TIME          = "send_time";
    public static final String API_SEND_CODE      = "api_send_code";
    public static final String API_SEND_MSG       = "api_send_msg";
    public static final String API_REQUEST_ID     = "api_request_id";
    public static final String API_SERIAL_NO      = "api_serial_no";
    public static final String RECEIVE_STATUS     = "receive_status";
    public static final String RECEIVE_TIME       = "receive_time";
    public static final String API_RECEIVE_CODE   = "api_receive_code";
    public static final String API_RECEIVE_MSG    = "api_receive_msg";

    // ========= 渠道相关字段 =========

    /**
     * 短信渠道编号
     */
    @Column(value = CHANNEL_ID)
    private Long channelId;
    /**
     * 短信渠道编码
     */
    @Column(value = CHANNEL_CODE)
    private String channelCode;

    // ========= 模板相关字段 =========

    /**
     * 模板编号
     */
    @Column(value = TEMPLATE_ID)
    private Long templateId;
    /**
     * 模板编码
     */
    @Column(value = TEMPLATE_CODE)
    private String templateCode;
    /**
     * 短信类型
     */
    @Column(value = TEMPLATE_TYPE)
    private Integer templateType;
    /**
     * 基于内容格式化后的内容
     */
    @Column(value = TEMPLATE_CONTENT)
    private String templateContent;
    /**
     * 基于参数生成的参数映射
     */
    @Column(value = TEMPLATE_PARAMS)
    private Map<String, Object> templateParams;
    /**
     * 短信 API 的模板编号
     */
    @Column(value = API_TEMPLATE_ID)
    private String apiTemplateId;

    // ========= 手机相关字段 =========

    /**
     * 手机号
     */
    @Column(value = MOBILE)
    private String mobile;
    /**
     * 用户编号
     */
    @Column(value = USER_ID)
    private Long userId;
    /**
     * 用户类型
     */
    @Column(value = USER_TYPE)
    private Integer userType;

    // ========= 发送相关字段 =========

    /**
     * 发送状态
     */
    @Column(value = SEND_STATUS)
    private Integer sendStatus;
    /**
     * 发送时间
     */
    @Column(value = SEND_TIME)
    private LocalDateTime sendTime;
    /**
     * 短信 API 发送结果的编码
     */
    @Column(value = API_SEND_CODE)
    private String apiSendCode;
    /**
     * 短信 API 发送失败的提示
     */
    @Column(value = API_SEND_MSG)
    private String apiSendMsg;
    /**
     * 短信 API 请求 ID
     */
    @Column(value = API_REQUEST_ID)
    private String apiRequestId;
    /**
     * 短信 API 序号
     */
    @Column(value = API_SERIAL_NO)
    private String apiSerialNo;

    // ========= 接收相关字段 =========

    /**
     * 接收状态
     */
    @Column(value = RECEIVE_STATUS)
    private Integer receiveStatus;
    /**
     * 接收时间
     */
    @Column(value = RECEIVE_TIME)
    private LocalDateTime receiveTime;
    /**
     * 接收结果编码
     */
    @Column(value = API_RECEIVE_CODE)
    private String apiReceiveCode;
    /**
     * 接收结果提示
     */
    @Column(value = API_RECEIVE_MSG)
    private String apiReceiveMsg;
}
