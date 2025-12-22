package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 角色和菜单关联表 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemRoleMenuTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 角色和菜单关联表
     */
    public static final SystemRoleMenuTableDef SYSTEM_ROLE_MENU = new SystemRoleMenuTableDef();

    /**
     * 自增编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 菜单ID
     */
    public final QueryColumn MENU_ID = new QueryColumn(this, "menu_id");

    /**
     * 角色ID
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

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
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, MENU_ID, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemRoleMenuTableDef() {
        super("", "system_role_menu");
    }

    private SystemRoleMenuTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemRoleMenuTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemRoleMenuTableDef("", "system_role_menu", alias));
    }

}
