package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 角色信息表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemRoleTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 角色信息表
     */
    public static final SystemRoleTableDef SYSTEM_ROLE = new SystemRoleTableDef();

    /**
     * 角色ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 角色权限字符串
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 角色名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 显示顺序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 角色类型(1内置，2普通)
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

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
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    public final QueryColumn DATA_SCOPE = new QueryColumn(this, "data_scope");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 数据范围(指定部门数组)
     */
    public final QueryColumn DATA_SCOPE_DEPT_IDS = new QueryColumn(this, "data_scope_dept_ids");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, CODE, SORT, DATA_SCOPE, DATA_SCOPE_DEPT_IDS, STATUS, TYPE, REMARK, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemRoleTableDef() {
        super("", "system_role");
    }

    private SystemRoleTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemRoleTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemRoleTableDef("", "system_role", alias));
    }

}
