package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 字典类型表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemDictTypeTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型表
     */
    public static final SystemDictTypeTableDef SYSTEM_DICT_TYPE = new SystemDictTypeTableDef();

    /**
     * 字典主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 字典名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 字典类型
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
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 删除时间
     */
    public final QueryColumn DELETED_TIME = new QueryColumn(this, "deleted_time");

    /**
     * 字典所有者ID（应用ID或租户ID）
     */
    public final QueryColumn DICT_OWNER_ID = new QueryColumn(this, "dict_owner_id");

    /**
     * 字典所有者类型（app-应用自定义字典，tenant-空间公共字典）
     */
    public final QueryColumn DICT_OWNER_TYPE = new QueryColumn(this, "dict_owner_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, TYPE, STATUS, REMARK, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, DELETED_TIME, DICT_OWNER_TYPE, DICT_OWNER_ID};

    public SystemDictTypeTableDef() {
        super("", "system_dict_type");
    }

    private SystemDictTypeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemDictTypeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemDictTypeTableDef("", "system_dict_type", alias));
    }

}
