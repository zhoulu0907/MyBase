package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 字典数据表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemDictDataTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 字典数据表
     */
    public static final SystemDictDataTableDef SYSTEM_DICT_DATA = new SystemDictDataTableDef();

    /**
     * 字典编码
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 字典排序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 字典标签
     */
    public final QueryColumn LABEL = new QueryColumn(this, "label");

    /**
     * 字典键值
     */
    public final QueryColumn VALUE = new QueryColumn(this, "value");

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
     * css 样式
     */
    public final QueryColumn CSS_CLASS = new QueryColumn(this, "css_class");

    /**
     * 字典类型
     */
    public final QueryColumn DICT_TYPE = new QueryColumn(this, "dict_type");

    /**
     * 颜色类型
     */
    public final QueryColumn COLOR_TYPE = new QueryColumn(this, "color_type");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SORT, LABEL, VALUE, DICT_TYPE, STATUS, COLOR_TYPE, CSS_CLASS, REMARK, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemDictDataTableDef() {
        super("", "system_dict_data");
    }

    private SystemDictDataTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemDictDataTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemDictDataTableDef("", "system_dict_data", alias));
    }

}
