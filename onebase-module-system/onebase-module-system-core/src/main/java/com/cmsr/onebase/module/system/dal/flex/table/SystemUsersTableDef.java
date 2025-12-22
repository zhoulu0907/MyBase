package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 用户信息表 表定义层。
 *
 * @author mty
 * @since 2025-12-22
 */
public class SystemUsersTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 用户信息表
     */
    public static final SystemUsersTableDef SYSTEM_USERS = new SystemUsersTableDef();

    /**
     * 用户ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 用户性别
     */
    public final QueryColumn SEX = new QueryColumn(this, "sex");

    /**
     * 用户邮箱
     */
    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    /**
     * 头像地址
     */
    public final QueryColumn AVATAR = new QueryColumn(this, "avatar");

    /**
     * 企业id
     */
    public final QueryColumn CORP_ID = new QueryColumn(this, "corp_id");

    /**
     * 部门ID
     */
    public final QueryColumn DEPT_ID = new QueryColumn(this, "dept_id");

    /**
     * 手机号码
     */
    public final QueryColumn MOBILE = new QueryColumn(this, "mobile");

    /**
     * 备注
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态（0停用，1启用）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 最后登录IP
     */
    public final QueryColumn LOGIN_IP = new QueryColumn(this, "login_ip");

    /**
     * 岗位编号数组
     */
    public final QueryColumn POST_IDS = new QueryColumn(this, "post_ids");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 用户昵称
     */
    public static final QueryColumn NICKNAME = new QueryColumn(this, "nickname");

    /**
     * 密码
     */
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 用户类型
     */
    public final QueryColumn USER_TYPE = new QueryColumn(this, "user_type");

    /**
     * 用户账号
     */
    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 管理员类型
     */
    public final QueryColumn ADMIN_TYPE = new QueryColumn(this, "admin_type");

    /**
     * 最后登录时间
     */
    public final QueryColumn LOGIN_DATE = new QueryColumn(this, "login_date");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 三方用户创建来源 后台back/自主 self
     */
    public final QueryColumn CREATE_SOURCE = new QueryColumn(this, "create_source");

    /**
     * 来自平台克隆的用户id
     */
    public final QueryColumn PLATFORM_USER_ID = new QueryColumn(this, "platform_user_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USERNAME, PASSWORD, NICKNAME, REMARK, DEPT_ID, POST_IDS, EMAIL, MOBILE, SEX, AVATAR, STATUS, LOGIN_IP, LOGIN_DATE, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, USER_TYPE, ADMIN_TYPE, CORP_ID, PLATFORM_USER_ID, CREATE_SOURCE};

    public SystemUsersTableDef() {
        super("", "system_users");
    }

    private SystemUsersTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemUsersTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemUsersTableDef("", "system_users", alias));
    }

}
