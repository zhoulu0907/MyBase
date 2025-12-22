package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 平台License信息 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemLicenseTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 平台License信息
     */
    public static final SystemLicenseTableDef SYSTEM_LICENSE = new SystemLicenseTableDef();

    /**
     * 主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 状态：enable,disable
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 创建者ID
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 软删标识：非0即删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 是否为试用License
     */
    public final QueryColumn IS_TRIAL = new QueryColumn(this, "is_trial");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 用户数量限制
     */
    public final QueryColumn USER_LIMIT = new QueryColumn(this, "user_limit");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 到期时间
     */
    public final QueryColumn EXPIRE_TIME = new QueryColumn(this, "expire_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * License文件
     */
    public final QueryColumn LICENSE_FILE = new QueryColumn(this, "license_file");

    /**
     * 租户数量限制
     */
    public final QueryColumn TENANT_LIMIT = new QueryColumn(this, "tenant_limit");

    /**
     * 平台类型
     */
    public final QueryColumn PLATFORM_TYPE = new QueryColumn(this, "platform_type");

    /**
     * 企业编号
     */
    public final QueryColumn ENTERPRISE_CODE = new QueryColumn(this, "enterprise_code");

    /**
     * 企业名称
     */
    public final QueryColumn ENTERPRISE_NAME = new QueryColumn(this, "enterprise_name");

    /**
     * 企业地址
     */
    public final QueryColumn ENTERPRISE_ADDRESS = new QueryColumn(this, "enterprise_address");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ENTERPRISE_NAME, ENTERPRISE_CODE, ENTERPRISE_ADDRESS, PLATFORM_TYPE, TENANT_LIMIT, USER_LIMIT, EXPIRE_TIME, STATUS, IS_TRIAL, LICENSE_FILE, CREATOR, UPDATER, CREATE_TIME, UPDATE_TIME, DELETED};

    public SystemLicenseTableDef() {
        super("", "system_license");
    }

    private SystemLicenseTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemLicenseTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemLicenseTableDef("", "system_license", alias));
    }

}
