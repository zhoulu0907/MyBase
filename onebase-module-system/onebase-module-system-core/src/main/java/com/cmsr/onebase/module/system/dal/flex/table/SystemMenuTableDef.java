package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 菜单权限表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemMenuTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单权限表
     */
    public static final SystemMenuTableDef SYSTEM_MENU = new SystemMenuTableDef();

    /**
     * 菜单ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 菜单图标
     */
    public final QueryColumn ICON = new QueryColumn(this, "icon");

    /**
     * 菜单名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 路由地址
     */
    public final QueryColumn PATH = new QueryColumn(this, "path");

    /**
     * 显示顺序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 菜单类型
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

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
     * 是否可见
     */
    public final QueryColumn VISIBLE = new QueryColumn(this, "visible");

    /**
     * 父菜单ID
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    /**
     * 组件路径
     */
    public final QueryColumn COMPONENT = new QueryColumn(this, "component");

    /**
     * 是否缓存
     */
    public final QueryColumn KEEP_ALIVE = new QueryColumn(this, "keep_alive");

    /**
     * 是否总是显示
     */
    public final QueryColumn ALWAYS_SHOW = new QueryColumn(this, "always_show");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 权限标识
     */
    public final QueryColumn PERMISSION = new QueryColumn(this, "permission");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 组件名
     */
    public final QueryColumn COMPONENT_NAME = new QueryColumn(this, "component_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PERMISSION, TYPE, SORT, PARENT_ID, PATH, ICON, COMPONENT, COMPONENT_NAME, STATUS, VISIBLE, KEEP_ALIVE, ALWAYS_SHOW, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemMenuTableDef() {
        super("", "system_menu");
    }

    private SystemMenuTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemMenuTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemMenuTableDef("", "system_menu", alias));
    }

}
