package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author zhoumingji
 * @since 2026-01-27
 */
public class AppExportTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public static final AppExportTableDef APP_EXPORT = new AppExportTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");


    public final QueryColumn CREATOR = new QueryColumn(this, "creator");


    public final QueryColumn DELETED = new QueryColumn(this, "deleted");


    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 资源ID
     */
    public final QueryColumn OBJECT_ID = new QueryColumn(this, "object_id");


    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");


    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");


    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");


    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 导出状态 0-未知 1-导出中 2-导出成功 3-导出失败
     */
    public final QueryColumn EXPORT_STATUS = new QueryColumn(this, "export_status");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, OBJECT_ID, EXPORT_STATUS, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppExportTableDef() {
        super("", "app_export");
    }

    private AppExportTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppExportTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppExportTableDef("", "app_export", alias));
    }

}
