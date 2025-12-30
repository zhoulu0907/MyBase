package com.cmsr.onebase.module.system.dal.dataobject.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDOInterface;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.module.system.dal.flex.typehandler.SetLongJsonTypeHandler;
import com.cmsr.onebase.module.system.enums.common.SexEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理后台的用户 DO
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Data
@Table("system_users")
public class AdminUserDO extends BaseTenantEntity implements BaseDOInterface {
    public static final String ID   = "id";
    public static final String USERNAME   = "username";
    // 新增各字段对应的常量
    public static final String PASSWORD   = "password";
    public static final String NICKNAME   = "nickname";
    public static final String REMARK     = "remark";
    public static final String DEPT_ID    = "dept_id";
    public static final String POST_IDS   = "post_ids";
    public static final String EMAIL      = "email";
    public static final String MOBILE     = "mobile";
    public static final String SEX        = "sex";
    public static final String AVATAR     = "avatar";
    public static final String STATUS     = "status";
    public static final String LOGIN_IP   = "login_ip";
    public static final String LOGIN_DATE = "login_date";
    public static final String USER_TYPE  = "user_type";
    public static final String ADMIN_TYPE = "admin_type";
    public static final String CORP_ID    = "corp_id";
    public static final String PLATFORM_USER_ID    = "platform_user_id";
    public static final String CREATE_SOURCE    = "create_source";


    public AdminUserDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 用户账号
     */
    @Column(USERNAME)
    private String username;

    /**
     * 加密后的密码
     * <p>
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
    @Column(PASSWORD)
    private String password;

    /**
     * 用户昵称
     */
    @Column(NICKNAME)
    private String nickname;

    /**
     * 备注
     */
    @Column(REMARK)
    private String remark;

    /**
     * 部门 ID
     */
    @Column(DEPT_ID)
    private Long deptId;

    /**
     * 岗位编号数组
     */
    @Column(value = POST_IDS, typeHandler = SetLongJsonTypeHandler.class)
    private Set<Long> postIds;

    /**
     * 用户邮箱
     */
    @Column(EMAIL)
    private String email;

    /**
     * 手机号码
     */
    @Column(MOBILE)
    private String mobile;

    /**
     * 用户性别
     * <p>
     * 枚举类 {@link SexEnum}
     */
    @Column(SEX)
    private Integer sex;

    /**
     * 用户头像
     */
    @Column(AVATAR)
    private String avatar;

    /**
     * 帐号状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(STATUS)
    private Integer status;

    /**
     * 最后登录IP
     */
    @Column(LOGIN_IP)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Column(LOGIN_DATE)
    private LocalDateTime loginDate;

    /**
     * 用户类型 see {@link com.cmsr.onebase.framework.common.enums.UserTypeEnum}
     */
    @Column(USER_TYPE)
    private Integer userType;

    /**
     * 管理员类型
     */
    @Column(ADMIN_TYPE)
    private Integer adminType;

    /**
     * 创建来源 后台创建/自主注册
     */
    @Column(CREATE_SOURCE)
    private String createSource;

    /**
     * 归属企业ID
     */
    @Column(CORP_ID)
    private Long corpId;

    /**
     * 来自平台的克隆的用户id
     */
    @Column(PLATFORM_USER_ID)
    private Long platformUserId;
}
