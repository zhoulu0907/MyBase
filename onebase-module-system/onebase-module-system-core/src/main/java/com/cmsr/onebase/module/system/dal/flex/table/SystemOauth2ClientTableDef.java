package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * OAuth2 客户端表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemOauth2ClientTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * OAuth2 客户端表
     */
    public static final SystemOauth2ClientTableDef SYSTEM_OAUTH2_CLIENT = new SystemOauth2ClientTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 应用图标
     */
    public final QueryColumn LOGO = new QueryColumn(this, "logo");

    /**
     * 应用名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 授权范围
     */
    public final QueryColumn SCOPES = new QueryColumn(this, "scopes");

    /**
     * 客户端密钥
     */
    public final QueryColumn SECRET = new QueryColumn(this, "secret");

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
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 客户端编号
     */
    public final QueryColumn CLIENT_ID = new QueryColumn(this, "client_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 权限
     */
    public final QueryColumn AUTHORITIES = new QueryColumn(this, "authorities");

    /**
     * 应用描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * 资源
     */
    public final QueryColumn RESOURCE_IDS = new QueryColumn(this, "resource_ids");

    /**
     * 可重定向的 URI 地址
     */
    public final QueryColumn REDIRECT_URIS = new QueryColumn(this, "redirect_uris");

    /**
     * 自动通过的授权范围
     */
    public final QueryColumn AUTO_APPROVE_SCOPES = new QueryColumn(this, "auto_approve_scopes");

    /**
     * 授权类型
     */
    public final QueryColumn AUTHORIZED_GRANT_TYPES = new QueryColumn(this, "authorized_grant_types");

    /**
     * 附加信息
     */
    public final QueryColumn ADDITIONAL_INFORMATION = new QueryColumn(this, "additional_information");

    /**
     * 访问令牌的有效期
     */
    public final QueryColumn ACCESS_TOKEN_VALIDITY_SECONDS = new QueryColumn(this, "access_token_validity_seconds");

    /**
     * 刷新令牌的有效期
     */
    public final QueryColumn REFRESH_TOKEN_VALIDITY_SECONDS = new QueryColumn(this, "refresh_token_validity_seconds");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CLIENT_ID, SECRET, NAME, LOGO, DESCRIPTION, STATUS, ACCESS_TOKEN_VALIDITY_SECONDS, REFRESH_TOKEN_VALIDITY_SECONDS, REDIRECT_URIS, AUTHORIZED_GRANT_TYPES, SCOPES, AUTO_APPROVE_SCOPES, AUTHORITIES, RESOURCE_IDS, ADDITIONAL_INFORMATION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemOauth2ClientTableDef() {
        super("", "system_oauth2_client");
    }

    private SystemOauth2ClientTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemOauth2ClientTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemOauth2ClientTableDef("", "system_oauth2_client", alias));
    }

}
