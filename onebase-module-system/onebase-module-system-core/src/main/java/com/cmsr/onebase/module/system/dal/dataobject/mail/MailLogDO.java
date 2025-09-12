package com.cmsr.onebase.module.system.dal.dataobject.mail;

import java.time.LocalDateTime;
import java.util.Map;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.enums.mail.MailSendStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 邮箱日志 DO
 * 记录每一次邮件的发送
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Table(name = "system_mail_log")
@Data
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class MailLogDO extends BaseDO {

    public static final String USER_ID           = "user_id";
    public static final String USER_TYPE         = "user_type";
    public static final String TO_MAIL           = "to_mail";
    public static final String ACCOUNT_ID        = "account_id";
    public static final String FROM_MAIL         = "from_mail";
    public static final String TEMPLATE_ID       = "template_id";
    public static final String TEMPLATE_CODE     = "template_code";
    public static final String TEMPLATE_NICKNAME = "template_nickname";
    public static final String TEMPLATE_TITLE    = "template_title";
    public static final String TEMPLATE_CONTENT  = "template_content";
    public static final String TEMPLATE_PARAMS   = "template_params";
    public static final String SEND_STATUS       = "send_status";
    public static final String SEND_TIME         = "send_time";
    public static final String SEND_MESSAGE_ID   = "send_message_id";
    public static final String SEND_EXCEPTION    = "send_exception";

    /**
     * 用户编码
     */
    @Column(name = USER_ID)
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(name = USER_TYPE)
    private Integer userType;
    /**
     * 接收邮箱地址
     */
    @Column(name = TO_MAIL)
    private String toMail;

    /**
     * 邮箱账号编号
     *
     * 关联 {@link MailAccountDO#getId()}
     */
    @Column(name = ACCOUNT_ID)
    private Long accountId;
    /**
     * 发送邮箱地址
     *
     * 冗余 {@link MailAccountDO#getMail()}
     */
    @Column(name = FROM_MAIL)
    private String fromMail;

    // ========= 模板相关字段 =========
    /**
     * 模版编号
     *
     * 关联 {@link MailTemplateDO#getId()}
     */
    @Column(name = TEMPLATE_ID)
    private Long templateId;
    /**
     * 模版编码
     *
     * 冗余 {@link MailTemplateDO#getCode()}
     */
    @Column(name = TEMPLATE_CODE)
    private String templateCode;
    /**
     * 模版发送人名称
     *
     * 冗余 {@link MailTemplateDO#getNickname()}
     */
    @Column(name = TEMPLATE_NICKNAME)
    private String templateNickname;
    /**
     * 模版标题
     */
    @Column(name = TEMPLATE_TITLE)
    private String templateTitle;
    /**
     * 模版内容
     *
     * 基于 {@link MailTemplateDO#getContent()} 格式化后的内容
     */
    @Column(name = TEMPLATE_CONTENT)
    private String templateContent;
    /**
     * 模版参数
     *
     * 基于 {@link MailTemplateDO#getParams()} 输入后的参数
     */
    @Column(name = TEMPLATE_PARAMS)
    private Map<String, Object> templateParams;

    // ========= 发送相关字段 =========
    /**
     * 发送状态
     *
     * 枚举 {@link MailSendStatusEnum}
     */
    @Column(name = SEND_STATUS)
    private Integer sendStatus;
    /**
     * 发送时间
     */
    @Column(name = SEND_TIME)
    private LocalDateTime sendTime;
    /**
     * 发送返回的消息 ID
     */
    @Column(name = SEND_MESSAGE_ID)
    private String sendMessageId;
    /**
     * 发送异常
     */
    @Column(name = SEND_EXCEPTION)
    private String sendException;

}
