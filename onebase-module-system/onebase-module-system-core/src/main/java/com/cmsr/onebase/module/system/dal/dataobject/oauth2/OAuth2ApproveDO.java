package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * OAuth2 批准 DO
 *
 * 用户在 sso.vue 界面时，记录接受的 scope 列表
 *
 */
@Table(name = "system_oauth2_approve")
@Data
public class OAuth2ApproveDO extends BaseDO {

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
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    @Column(name = CLIENT_ID)
    private String clientId;
    /**
     * 授权范围
     */
    @Column(name = SCOPE)
    private String scope;
    /**
     * 是否接受
     *
     * true - 接受
     * false - 拒绝
     */
    @Column(name = APPROVED)
    private Integer approved;
    /**
     * 过期时间
     */
    @Column(name = EXPIRES_TIME)
    private LocalDateTime expiresTime;

}
