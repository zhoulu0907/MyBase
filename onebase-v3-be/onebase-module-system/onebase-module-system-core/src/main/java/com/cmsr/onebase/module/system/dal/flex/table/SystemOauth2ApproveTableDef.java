package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * OAuth2 批准表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemOauth2ApproveTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * OAuth2 批准表
     */
    public static final SystemOauth2ApproveTableDef SYSTEM_OAUTH2_APPROVE = new SystemOauth2ApproveTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 授权范围
     */
    public final QueryColumn SCOPE = new QueryColumn(this, "scope");

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
     * 是否接受
     */
    public final QueryColumn APPROVED = new QueryColumn(this, "approved");

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
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, USER_TYPE, CLIENT_ID, SCOPE, APPROVED, EXPIRES_TIME, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemOauth2ApproveTableDef() {
        super("", "system_oauth2_approve");
    }

    private SystemOauth2ApproveTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemOauth2ApproveTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemOauth2ApproveTableDef("", "system_oauth2_approve", alias));
    }

}
