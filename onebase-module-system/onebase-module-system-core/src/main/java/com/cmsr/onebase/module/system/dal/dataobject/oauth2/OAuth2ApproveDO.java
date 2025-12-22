package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * OAuth2 批准 DO
 *
 * 用户在 sso.vue 界面时，记录接受的 scope 列表
 *
 */
@Table(value = "system_oauth2_approve")
@Data
public class OAuth2ApproveDO extends BaseEntity {

    public static final String USER_ID      = "user_id";
    public static final String USER_TYPE    = "user_type";
    public static final String CLIENT_ID    = "client_id";
    public static final String SCOPE        = "scope";
    public static final String APPROVED     = "approved";
    public static final String EXPIRES_TIME = "expires_time";

    public OAuth2ApproveDO setId(Long id){
        super.setId(id);
        return this;
    }
    /**
     * 用户编号
     */
    @Column(value = USER_ID)
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(value = USER_TYPE)
    private Integer userType;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    @Column(value = CLIENT_ID)
    private String clientId;
    /**
     * 授权范围
     */
    @Column(value = SCOPE)
    private String scope;
    /**
     * 是否接受
     *
     * true - 接受
     * false - 拒绝
     */
    @Column(value = APPROVED)
    private Integer approved;
    /**
     * 过期时间
     */
    @Column(value = EXPIRES_TIME)
    private LocalDateTime expiresTime;

}
