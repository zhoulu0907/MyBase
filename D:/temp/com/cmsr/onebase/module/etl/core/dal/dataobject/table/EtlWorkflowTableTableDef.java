package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * ETL工作流与表关联关系表 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
public class EtlWorkflowTableTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ETL工作流与表关联关系表
     */
    public static final EtlWorkflowTableTableDef ETL_WORKFLOW_TABLE = new EtlWorkflowTableTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn TABLE_ID = new QueryColumn(this, "table_id");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn RELATION = new QueryColumn(this, "relation");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn WORKFLOW_ID = new QueryColumn(this, "workflow_id");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn DATASOURCE_ID = new QueryColumn(this, "datasource_id");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, WORKFLOW_ID, RELATION, TABLE_ID, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION, DATASOURCE_ID};

    public EtlWorkflowTableTableDef() {
        super("", "etl_workflow_table");
    }

    private EtlWorkflowTableTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlWorkflowTableTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlWorkflowTableTableDef("", "etl_workflow_table", alias));
    }

}
