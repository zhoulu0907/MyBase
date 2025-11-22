package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
public class EtlFlinkFunctionTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final EtlFlinkFunctionTableDef ETL_FLINK_FUNCTION = new EtlFlinkFunctionTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn FUNCTION_DESC = new QueryColumn(this, "function_desc");

    
    public final QueryColumn FUNCTION_NAME = new QueryColumn(this, "function_name");

    
    public final QueryColumn FUNCTION_TYPE = new QueryColumn(this, "function_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, FUNCTION_TYPE, FUNCTION_NAME, FUNCTION_DESC, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION};

    public EtlFlinkFunctionTableDef() {
        super("", "etl_flink_function");
    }

    private EtlFlinkFunctionTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlFlinkFunctionTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlFlinkFunctionTableDef("", "etl_flink_function", alias));
    }

}
