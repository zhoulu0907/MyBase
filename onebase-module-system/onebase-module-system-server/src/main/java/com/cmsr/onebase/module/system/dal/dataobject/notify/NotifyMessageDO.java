package com.cmsr.onebase.module.system.dal.dataobject.notify;

import java.time.LocalDateTime;
import java.util.Map;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 站内信 DO
 *
 * @author xrcoder
 */
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_notify_message")
public class NotifyMessageDO extends BaseDO {

    // 字段常量
    public static final String USER_ID             = "user_id";
    public static final String USER_TYPE           = "user_type";
    public static final String TEMPLATE_ID         = "template_id";
    public static final String TEMPLATE_CODE       = "template_code";
    public static final String TEMPLATE_TYPE       = "template_type";
    public static final String TEMPLATE_NICKNAME   = "template_nickname";
    public static final String TEMPLATE_CONTENT    = "template_content";
    public static final String TEMPLATE_PARAMS     = "template_params";
    public static final String READ_STATUS         = "read_status";
    public static final String READ_TIME           = "read_time";

    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 字段、或者 AdminUserDO 的 id 字段
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

    // ========= 模板相关字段 =========

    /**
     * 模版编号
     *
     * 关联 {@link NotifyTemplateDO#getId()}
     */
    @Column(name = TEMPLATE_ID)
    private Long templateId;
    /**
     * 模版编码
     *
     * 关联 {@link NotifyTemplateDO#getCode()}
     */
    @Column(name = TEMPLATE_CODE)
    private String templateCode;
    /**
     * 模版类型
     *
     * 冗余 {@link NotifyTemplateDO#getType()}
     */
    @Column(name = TEMPLATE_TYPE)
    private Integer templateType;
    /**
     * 模版发送人名称
     *
     * 冗余 {@link NotifyTemplateDO#getNickname()}
     */
    @Column(name = TEMPLATE_NICKNAME)
    private String templateNickname;
    /**
     * 模版内容
     *
     * 基于 {@link NotifyTemplateDO#getContent()} 格式化后的内容
     */
    @Column(name = TEMPLATE_CONTENT)
    private String templateContent;
    /**
     * 模版参数
     *
     * 基于 {@link NotifyTemplateDO#getParams()} 输入后的参数
     */
    @Column(name = TEMPLATE_PARAMS)
    private Map<String, Object> templateParams;

    // ========= 读取相关字段 =========

    /**
     * 是否已读
     */
    @Column(name = READ_STATUS)
    private Boolean readStatus;
    /**
     * 阅读时间
     */
    @Column(name = READ_TIME)
    private LocalDateTime readTime;

}
