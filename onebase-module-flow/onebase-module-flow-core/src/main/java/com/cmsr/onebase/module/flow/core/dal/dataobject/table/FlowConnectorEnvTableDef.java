package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * flow_connector_env 表定义层
 * <p>
 * 定义 flow_connector_env 表的字段常量，用于类型安全的查询构建
 *
 * @author zhoulu
 * @since 2026-01-23
 */
public class FlowConnectorEnvTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 表实例
     */
    public static final FlowConnectorEnvTableDef FLOW_CONNECTOR_ENV = new FlowConnectorEnvTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 环境配置UUID
     */
    public final QueryColumn ENV_UUID = new QueryColumn(this, "env_uuid");

    /**
     * 环境名称
     */
    public final QueryColumn ENV_NAME = new QueryColumn(this, "env_name");

    /**
     * 环境编码
     */
    public final QueryColumn ENV_CODE = new QueryColumn(this, "env_code");

    /**
     * 连接器类型编号
     */
    public final QueryColumn TYPE_CODE = new QueryColumn(this, "type_code");

    /**
     * 环境URL
     */
    public final QueryColumn ENV_URL = new QueryColumn(this, "env_url");

    /**
     * 认证方式
     */
    public final QueryColumn AUTH_TYPE = new QueryColumn(this, "auth_type");

    /**
     * 认证配置
     */
    public final QueryColumn AUTH_CONFIG = new QueryColumn(this, "auth_config");

    /**
     * 环境描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * 扩展配置
     */
    public final QueryColumn EXTRA_CONFIG = new QueryColumn(this, "extra_config");

    /**
     * 启用状态
     */
    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");

    /**
     * 排序序号
     */
    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    /**
     * 租户ID
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 创建人
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新人
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 删除标识
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 乐观锁版本号
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 所有字段
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段（不包含逻辑删除等字段）
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{
            ID, ENV_UUID, ENV_NAME, ENV_CODE, TYPE_CODE, ENV_URL, AUTH_TYPE,
            AUTH_CONFIG, DESCRIPTION, EXTRA_CONFIG, ACTIVE_STATUS, SORT_ORDER,
            TENANT_ID, APPLICATION_ID, CREATOR, CREATE_TIME, UPDATER,
            UPDATE_TIME, DELETED, LOCK_VERSION
    };

    /**
     * 默认构造函数
     */
    public FlowConnectorEnvTableDef() {
        super("", "flow_connector_env");
    }

    /**
     * 带别名构造函数
     */
    private FlowConnectorEnvTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    /**
     * 创建别名
     */
    public FlowConnectorEnvTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FlowConnectorEnvTableDef("", "flow_connector_env", alias));
    }
}
