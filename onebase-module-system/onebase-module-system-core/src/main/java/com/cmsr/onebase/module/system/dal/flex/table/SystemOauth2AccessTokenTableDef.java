package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * OAuth2 访问令牌 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemOauth2AccessTokenTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * OAuth2 访问令牌
     */
    public static final SystemOauth2AccessTokenTableDef SYSTEM_OAUTH2_ACCESS_TOKEN = new SystemOauth2AccessTokenTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * AppID
     */
    public final QueryColumn APP_ID = new QueryColumn(this, "app_id");

    /**
     * 企业ID
     */
    public final QueryColumn CORP_ID = new QueryColumn(this, "corp_id");

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
     * 运行模式
     */
    public final QueryColumn RUN_MODE = new QueryColumn(this, "run_mode");

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
     * 用户信息
     */
    public final QueryColumn USER_INFO = new QueryColumn(this, "user_info");

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
     * 访问令牌
     */
    public final QueryColumn ACCESS_TOKEN = new QueryColumn(this, "access_token");

    /**
     * 过期时间
     */
    public final QueryColumn EXPIRES_TIME = new QueryColumn(this, "expires_time");

    /**
     * 刷新令牌
     */
    public final QueryColumn REFRESH_TOKEN = new QueryColumn(this, "refresh_token");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, USER_TYPE, USER_INFO, ACCESS_TOKEN, REFRESH_TOKEN, CLIENT_ID, SCOPES, EXPIRES_TIME, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, CORP_ID, APP_ID, RUN_MODE};

    public SystemOauth2AccessTokenTableDef() {
        super("", "system_oauth2_access_token");
    }

    private SystemOauth2AccessTokenTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemOauth2AccessTokenTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemOauth2AccessTokenTableDef("", "system_oauth2_access_token", alias));
    }

}
