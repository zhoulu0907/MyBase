package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 操作日志记录 V2 版本 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemOperateLogTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 操作日志记录 V2 版本
     */
    public static final SystemOperateLogTableDef SYSTEM_OPERATE_LOG = new SystemOperateLogTableDef();

    /**
     * 日志主键
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 操作模块类型
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

    /**
     * 操作数据模块编号
     */
    public final QueryColumn BIZ_ID = new QueryColumn(this, "biz_id");

    /**
     * 拓展字段
     */
    public final QueryColumn EXTRA = new QueryColumn(this, "extra");

    /**
     * 操作内容
     */
    public final QueryColumn ACTION = new QueryColumn(this, "action");

    /**
     * 用户编号
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 用户 IP
     */
    public final QueryColumn USER_IP = new QueryColumn(this, "user_ip");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 操作名
     */
    public final QueryColumn SUB_TYPE = new QueryColumn(this, "sub_type");

    /**
     * 链路追踪编号
     */
    public final QueryColumn TRACE_ID = new QueryColumn(this, "trace_id");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 用户类型
     */
    public final QueryColumn USER_TYPE = new QueryColumn(this, "user_type");

    /**
     * 浏览器 UA
     */
    public final QueryColumn USER_AGENT = new QueryColumn(this, "user_agent");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 请求地址
     */
    public final QueryColumn REQUEST_URL = new QueryColumn(this, "request_url");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 请求方法名
     */
    public final QueryColumn REQUEST_METHOD = new QueryColumn(this, "request_method");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TRACE_ID, USER_ID, USER_TYPE, TYPE, SUB_TYPE, BIZ_ID, ACTION, EXTRA, REQUEST_METHOD, REQUEST_URL, USER_IP, USER_AGENT, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemOperateLogTableDef() {
        super("", "system_operate_log");
    }

    private SystemOperateLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemOperateLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemOperateLogTableDef("", "system_operate_log", alias));
    }

}
