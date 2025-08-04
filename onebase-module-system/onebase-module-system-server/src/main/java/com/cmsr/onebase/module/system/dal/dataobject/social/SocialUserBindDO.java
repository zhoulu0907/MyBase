package com.cmsr.onebase.module.system.dal.dataobject.social;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

/**
 * 社交用户的绑定
 * 即 {@link SocialUserDO} 与 UserDO 的关联表
 *
 */
@Table(name = "system_social_user_bind")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindDO extends BaseDO {

    // 字段常量
    public static final String USER_ID        = "user_id";
    public static final String USER_TYPE      = "user_type";
    public static final String SOCIAL_USER_ID = "social_user_id";
    public static final String SOCIAL_TYPE    = "social_type";

    /**
     * 关联的用户编号
     *
     * 关联 UserDO 的编号
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
     * 社交平台的用户编号
     *
     * 关联 {@link SocialUserDO#getId()}
     */
    @Column(name = SOCIAL_USER_ID)
    private Long socialUserId;
    /**
     * 社交平台的类型
     *
     * 冗余 {@link SocialUserDO#getType()}
     */
    @Column(name = SOCIAL_TYPE)
    private Integer socialType;

}
