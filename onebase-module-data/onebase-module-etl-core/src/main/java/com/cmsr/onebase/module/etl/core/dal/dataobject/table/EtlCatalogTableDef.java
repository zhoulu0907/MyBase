package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 元数据采集-Catalog信息 表定义层。
 *
 * @author HuangJie
 * @since 2025-11-22
 */
public class EtlCatalogTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 元数据采集-Catalog信息
     */
    public static final EtlCatalogTableDef ETL_CATALOG = new EtlCatalogTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除（逻辑删除）
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 采集到的描述
     */
    public final QueryColumn REMARKS = new QueryColumn(this, "remarks");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 采集到的信息
     */
    public final QueryColumn META_INFO = new QueryColumn(this, "meta_info");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * catalog名称
     */
    public final QueryColumn CATALOG_NAME = new QueryColumn(this, "catalog_name");

    /**
     * 描述（用户可修改）
     */
    public final QueryColumn DECLARATION = new QueryColumn(this, "declaration");

    /**
     * 展示名称（用户可修改）
     */
    public final QueryColumn DISPLAY_NAME = new QueryColumn(this, "display_name");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 数据源Id
     */
    public final QueryColumn DATASOURCE_ID = new QueryColumn(this, "datasource_id");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, DATASOURCE_ID, CATALOG_NAME, DISPLAY_NAME, META_INFO, REMARKS, DECLARATION, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION};

    public EtlCatalogTableDef() {
        super("", "etl_catalog");
    }

    private EtlCatalogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlCatalogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlCatalogTableDef("", "etl_catalog", alias));
    }

}
