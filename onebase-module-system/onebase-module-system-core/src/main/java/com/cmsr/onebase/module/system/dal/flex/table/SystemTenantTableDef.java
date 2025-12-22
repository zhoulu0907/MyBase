package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 租户/空间表 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemTenantTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 租户/空间表
     */
    public static final SystemTenantTableDef SYSTEM_TENANT = new SystemTenantTableDef();

    /**
     * 租户编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 租户名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

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
     * 用户logo
     */
    public final QueryColumn LOGO_URL = new QueryColumn(this, "logo_url");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 绑定域名
     */
    public final QueryColumn WEBSITE = new QueryColumn(this, "website");

    /**
     * 访问地址
     */
    public final QueryColumn ACCESS_URL = new QueryColumn(this, "access_url");

    /**
     * 租户套餐编号
     */
    public final QueryColumn PACKAGE_ID = new QueryColumn(this, "package_id");

    /**
     * 租户key
     */
    public final QueryColumn TENANT_KEY = new QueryColumn(this, "tenant_key");

    /**
     * 移动端访问地址
     */
    public final QueryColumn WEBSITE_H5 = new QueryColumn(this, "website_h5");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 过期时间
     */
    public final QueryColumn EXPIRE_TIME = new QueryColumn(this, "expire_time");

    /**
     * 租户编码
     */
    public final QueryColumn TENANT_CODE = new QueryColumn(this, "tenant_code");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 联系人的用户编号
     */
    public final QueryColumn ADMIN_USER_ID = new QueryColumn(this, "admin_user_id");

    /**
     * 账号数量/人员数量/用户上线
     */
    public final QueryColumn ACCOUNT_COUNT = new QueryColumn(this, "account_count");

    /**
     * inner,saas
     */
    public final QueryColumn PUBLISH_MODEL = new QueryColumn(this, "publish_model");

    /**
     * 租住secret
     */
    public final QueryColumn TENANT_SECRET = new QueryColumn(this, "tenant_secret");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, ADMIN_USER_ID, STATUS, WEBSITE, PACKAGE_ID, EXPIRE_TIME, ACCOUNT_COUNT, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_CODE, WEBSITE_H5, TENANT_KEY, TENANT_SECRET, ACCESS_URL, PUBLISH_MODEL, LOGO_URL};

    public SystemTenantTableDef() {
        super("", "system_tenant");
    }

    private SystemTenantTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemTenantTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemTenantTableDef("", "system_tenant", alias));
    }

}
