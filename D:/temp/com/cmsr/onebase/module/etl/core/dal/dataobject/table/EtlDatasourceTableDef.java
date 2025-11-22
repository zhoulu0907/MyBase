package com.cmsr.onebase.module.etl.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * ETL数据源配置 表定义层。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
public class EtlDatasourceTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ETL数据源配置
     */
    public static final EtlDatasourceTableDef ETL_DATASOURCE = new EtlDatasourceTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 数据源配置信息（JSON）
     */
    public final QueryColumn CONFIG = new QueryColumn(this, "config");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除（逻辑删除）
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 是否只读数据源
     */
    public final QueryColumn READONLY = new QueryColumn(this, "readonly");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 数据源描述
     */
    public final QueryColumn DECLARATION = new QueryColumn(this, "declaration");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用Id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 采集状态，枚举值(none,required,success,failed,running)，默认为none
     */
    public final QueryColumn COLLECT_STATUS = new QueryColumn(this, "collect_status");

    /**
     * 采集结束时间
     */
    public final QueryColumn COLLECT_END_TIME = new QueryColumn(this, "collect_end_time");

    /**
     * 数据源编码
     */
    public final QueryColumn DATASOURCE_CODE = new QueryColumn(this, "datasource_code");

    /**
     * 数据源名称
     */
    public final QueryColumn DATASOURCE_NAME = new QueryColumn(this, "datasource_name");

    /**
     * 数据源类型，比如MySQL、PostgreSQL等
     */
    public final QueryColumn DATASOURCE_TYPE = new QueryColumn(this, "datasource_type");

    /**
     * 采集开始时间
     */
    public final QueryColumn COLLECT_START_TIME = new QueryColumn(this, "collect_start_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, DATASOURCE_CODE, DATASOURCE_NAME, DATASOURCE_TYPE, CONFIG, COLLECT_STATUS, COLLECT_START_TIME, COLLECT_END_TIME, READONLY, DELETED, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, LOCK_VERSION, DECLARATION};

    public EtlDatasourceTableDef() {
        super("", "etl_datasource");
    }

    private EtlDatasourceTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public EtlDatasourceTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new EtlDatasourceTableDef("", "etl_datasource", alias));
    }

}
