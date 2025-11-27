package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-26
 */
public class EtlFlinkMappingTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlFlinkMappingTableDef ETL_FLINK_MAPPING = new EtlFlinkMappingTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 对应Flink类型
     */
    public final QueryColumn FLINK_TYPE = new QueryColumn(this, "flink_type");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 原始列类型
     */
    public final QueryColumn ORIGIN_TYPE = new QueryColumn(this, "origin_type");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 数据库类型
     */
    public final QueryColumn DATASOURCE_TYPE = new QueryColumn(this, "datasource_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DATASOURCE_TYPE, ORIGIN_TYPE, FLINK_TYPE, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION};

    public EtlFlinkMappingTableDef() {
        super("", "etl_flink_mapping");
    }

    private EtlFlinkMappingTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlFlinkMappingTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlFlinkMappingTableDef("", "etl_flink_mapping", alias));
    }

}
