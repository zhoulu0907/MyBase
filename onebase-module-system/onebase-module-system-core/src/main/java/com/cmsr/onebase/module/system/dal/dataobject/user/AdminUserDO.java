package com.cmsr.onebase.module.system.dal.dataobject.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.system.enums.common.SexEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理后台的用户 DO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_users")
public class AdminUserDO extends TenantBaseDO {
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

    public AdminUserDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 用户账号
     */
    @Column(name = USERNAME)
    private String username;

    /**
     * 加密后的密码
     * <p>
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
    @Column(name = PASSWORD)
    private String password;

    /**
     * 用户昵称
     */
    @Column(name = NICKNAME)
    private String nickname;

    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

    /**
     * 部门 ID
     */
    @Column(name = DEPT_ID)
    private Long deptId;

    /**
     * 岗位编号数组
     */
    // @Transient //fixed NullP问题: 2025/7/20 需要以数组方式插入库里，anyline可能有bug导致null pointer，暂时Transient屏蔽
    @Column(name = POST_IDS)
    private Set<Long> postIds;

    /**
     * 用户邮箱
     */
    @Column(name = EMAIL)
    private String email;

    /**
     * 手机号码
     */
    @Column(name = MOBILE)
    private String mobile;

    /**
     * 用户性别
     * <p>
     * 枚举类 {@link SexEnum}
     */
    @Column(name = SEX)
    private Integer sex;

    /**
     * 用户头像
     */
    @Column(name = AVATAR)
    private String avatar;

    /**
     * 帐号状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;

    /**
     * 最后登录IP
     */
    @Column(name = LOGIN_IP)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Column(name = LOGIN_DATE)
    private LocalDateTime loginDate;

    /**
     * 用户类型
     */
    @Column(name = USER_TYPE)
    private Integer userType;

    /**
     * 管理员类型
     */
    @Column(name = ADMIN_TYPE)
    private Integer adminType;

    /**
     * 企业状态
     */
    private String statusDesc;

    /**
     * 归属企业ID
     */
    @Column(name = CORP_ID)
    private Long corpId;
}
