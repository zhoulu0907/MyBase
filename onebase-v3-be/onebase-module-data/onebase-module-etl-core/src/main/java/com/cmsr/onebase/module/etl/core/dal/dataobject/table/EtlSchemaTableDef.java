package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-26
 */
public class EtlSchemaTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlSchemaTableDef ETL_SCHEMA = new EtlSchemaTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn REMARKS = new QueryColumn(this, "remarks");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn META_INFO = new QueryColumn(this, "meta_info");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn SCHEMA_NAME = new QueryColumn(this, "schema_name");

    
    public final QueryColumn SCHEMA_UUID = new QueryColumn(this, "schema_uuid");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn CATALOG_UUID = new QueryColumn(this, "catalog_uuid");

    
    public final QueryColumn DECLARATION = new QueryColumn(this, "declaration");

    
    public final QueryColumn DISPLAY_NAME = new QueryColumn(this, "display_name");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    
    public final QueryColumn DATASOURCE_UUID = new QueryColumn(this, "datasource_uuid");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, DATASOURCE_UUID, CATALOG_UUID, SCHEMA_UUID, SCHEMA_NAME, DISPLAY_NAME, META_INFO, REMARKS, DECLARATION, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION, TENANT_ID};

    public EtlSchemaTableDef() {
        super("", "etl_schema");
    }

    private EtlSchemaTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlSchemaTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlSchemaTableDef("", "etl_schema", alias));
    }

}
