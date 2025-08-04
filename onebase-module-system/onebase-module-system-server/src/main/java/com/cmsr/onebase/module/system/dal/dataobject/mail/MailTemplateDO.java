package com.cmsr.onebase.module.system.dal.dataobject.mail;

import java.util.List;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 邮件模版 DO
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Table(name = "system_mail_template")
@Data
@TenantIgnore
public class MailTemplateDO extends BaseDO {

    public static final String NAME       = "name";
    public static final String CODE       = "code";
    public static final String ACCOUNT_ID = "account_id";
    public static final String NICKNAME   = "nickname";
    public static final String TITLE      = "title";
    public static final String CONTENT    = "content";
    public static final String PARAMS     = "params";
    public static final String STATUS     = "status";
    public static final String REMARK     = "remark";

    /**
     * 模版名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 模版编号
     */
    @Column(name = CODE)
    private String code;
    /**
     * 发送的邮箱账号编号
     *
     * 关联 {@link MailAccountDO#getId()}
     */
    @Column(name = ACCOUNT_ID)
    private Long accountId;

    /**
     * 发送人名称
     */
    @Column(name = NICKNAME)
    private String nickname;
    /**
     * 标题
     */
    @Column(name = TITLE)
    private String title;
    /**
     * 内容
     */
    @Column(name = CONTENT)
    private String content;
    /**
     * 参数数组(自动根据内容生成)
     */
    @Column(name = PARAMS)
    private List<String> params;
    /**
     * 状态
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

}
