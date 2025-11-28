package com.cmsr.onebase.framework.common.security.dto;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 *
 */
@Data
public class LoginUser {

    public static final String INFO_KEY_NICKNAME = "nickname";
    public static final String INFO_KEY_DEPT_ID = "deptId";

    /**
     * 用户编号
     */
    private Long id;
    /**
     * 用户类型
     *
     * 关联 {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * 登录用户 租户ID
     */
    private Long tenantId;

    /**
     * 登录用户 企业ID
     */
    private Long corpId;

    /**
     * 额外的用户信息
     */
    private Map<String, String> info;
    /**
     * 授权范围
     */
    private List<String> scopes;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;
    
    /**
     * 访问的租户编号
     */
    private Long visitTenantId;

    /**
     * 运行模式，see {@link com.cmsr.onebase.framework.common.enums.RunModeEnum}
     */
    private String runMode;

}
