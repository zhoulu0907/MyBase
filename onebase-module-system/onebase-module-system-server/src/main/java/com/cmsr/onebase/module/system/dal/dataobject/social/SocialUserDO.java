package com.cmsr.onebase.module.system.dal.dataobject.social;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.enums.social.SocialTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 社交（三方）用户
 *
 * @author weir
 */
@Table(name = "system_social_user")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserDO extends BaseDO {

    // 字段常量
    public static final String TYPE          = "type";
    public static final String OPENID        = "openid";
    public static final String TOKEN         = "token";
    public static final String RAW_TOKEN_INFO= "raw_token_info";
    public static final String NICKNAME      = "nickname";
    public static final String AVATAR        = "avatar";
    public static final String RAW_USER_INFO = "raw_user_info";
    public static final String CODE          = "code";
    public static final String STATE         = "state";

    /**
     * 社交平台的类型
     *
     * 枚举 {@link SocialTypeEnum}
     */
    @Column(name = TYPE)
    private Integer type;

    /**
     * 社交 openid
     */
    @Column(name = OPENID)
    private String openid;
    /**
     * 社交 token
     */
    @Column(name = TOKEN)
    private String token;
    /**
     * 原始 Token 数据，一般是 JSON 格式
     */
    @Column(name = RAW_TOKEN_INFO)
    private String rawTokenInfo;

    /**
     * 用户昵称
     */
    @Column(name = NICKNAME)
    private String nickname;
    /**
     * 用户头像
     */
    @Column(name = AVATAR)
    private String avatar;
    /**
     * 原始用户数据，一般是 JSON 格式
     */
    @Column(name = RAW_USER_INFO)
    private String rawUserInfo;

    /**
     * 最后一次的认证 code
     */
    @Column(name = CODE)
    private String code;
    /**
     * 最后一次的认证 state
     */
    @Column(name = STATE)
    private String state;

}


