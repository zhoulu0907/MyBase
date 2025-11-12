package com.cmsr.onebase.module.system.dal.dataobject.mail;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import lombok.Data;

/**
 * 邮箱账号 DO
 *
 * 用途：配置发送邮箱的账号
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Table(name = "system_mail_account")
@Data
@TenantIgnore
public class MailAccountDO extends BaseDO {

    public static final String MAIL             = "mail";
    public static final String USERNAME         = "username";
    public static final String PASSWORD         = "password";
    public static final String HOST             = "host";
    public static final String PORT             = "port";
    public static final String SSL_ENABLE       = "ssl_enable";
    public static final String STARTTLS_ENABLE  = "starttls_enable";

    /**
     * 邮箱
     */
    @Column(name = MAIL)
    private String mail;

    /**
     * 用户名
     */
    @Column(name = USERNAME)
    private String username;
    /**
     * 密码
     */
    @Column(name = PASSWORD)
    private String password;
    /**
     * SMTP 服务器域名
     */
    @Column(name = HOST)
    private String host;
    /**
     * SMTP 服务器端口
     */
    @Column(name = PORT)
    private Integer port;
    /**
     * 是否开启 SSL
     */
    @Column(name = SSL_ENABLE)
    private Integer sslEnable;
    /**
     * 是否开启 STARTTLS
     */
    @Column(name = STARTTLS_ENABLE)
    private Integer starttlsEnable;

}
