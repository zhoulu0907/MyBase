package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 租户套餐表 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemTenantPackageTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 租户套餐表
     */
    public static final SystemTenantPackageTableDef SYSTEM_TENANT_PACKAGE = new SystemTenantPackageTableDef();

    /**
     * 套餐编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 租户套餐编码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 套餐名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

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
     * 关联的菜单IDS
     */
    public final QueryColumn MENU_IDS = new QueryColumn(this, "menu_ids");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, STATUS, REMARK, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, MENU_IDS, CODE};

    public SystemTenantPackageTableDef() {
        super("", "system_tenant_package");
    }

    private SystemTenantPackageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemTenantPackageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemTenantPackageTableDef("", "system_tenant_package", alias));
    }

}
