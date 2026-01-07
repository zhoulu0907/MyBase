package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * OAuth2 授权码表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemOauth2CodeTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * OAuth2 授权码表
     */
    public static final SystemOauth2CodeTableDef SYSTEM_OAUTH2_CODE = new SystemOauth2CodeTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 授权码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 状态
     */
    public final QueryColumn STATE = new QueryColumn(this, "state");

    /**
     * 授权范围
     */
    public final QueryColumn SCOPES = new QueryColumn(this, "scopes");

    /**
     * 用户编号
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 客户端编号
     */
    public final QueryColumn CLIENT_ID = new QueryColumn(this, "client_id");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 用户类型
     */
    public final QueryColumn USER_TYPE = new QueryColumn(this, "user_type");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 过期时间
     */
    public final QueryColumn EXPIRES_TIME = new QueryColumn(this, "expires_time");

    /**
     * 可重定向的 URI 地址
     */
    public final QueryColumn REDIRECT_URI = new QueryColumn(this, "redirect_uri");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, USER_TYPE, CODE, CLIENT_ID, SCOPES, EXPIRES_TIME, REDIRECT_URI, STATE, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemOauth2CodeTableDef() {
        super("", "system_oauth2_code");
    }

    private SystemOauth2CodeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemOauth2CodeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemOauth2CodeTableDef("", "system_oauth2_code", alias));
    }

}
