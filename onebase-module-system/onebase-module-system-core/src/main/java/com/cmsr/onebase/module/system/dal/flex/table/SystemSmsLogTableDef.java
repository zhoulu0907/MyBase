package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 短信日志 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemSmsLogTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 短信日志
     */
    public static final SystemSmsLogTableDef SYSTEM_SMS_LOG = new SystemSmsLogTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 手机号
     */
    public final QueryColumn MOBILE = new QueryColumn(this, "mobile");

    /**
     * 用户编号
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

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
     * 发送时间
     */
    public final QueryColumn SEND_TIME = new QueryColumn(this, "send_time");

    /**
     * 用户类型
     */
    public final QueryColumn USER_TYPE = new QueryColumn(this, "user_type");

    /**
     * 短信渠道编号
     */
    public final QueryColumn CHANNEL_ID = new QueryColumn(this, "channel_id");

    /**
     * 短信 API 发送失败的提示
     */
    public final QueryColumn API_SEND_MSG = new QueryColumn(this, "api_send_msg");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 发送状态
     */
    public final QueryColumn SEND_STATUS = new QueryColumn(this, "send_status");

    /**
     * 模板编号
     */
    public final QueryColumn TEMPLATE_ID = new QueryColumn(this, "template_id");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 短信 API 发送结果的编码
     */
    public final QueryColumn API_SEND_CODE = new QueryColumn(this, "api_send_code");

    /**
     * 短信 API 发送返回的序号
     */
    public final QueryColumn API_SERIAL_NO = new QueryColumn(this, "api_serial_no");

    /**
     * 短信渠道编码
     */
    public final QueryColumn CHANNEL_CODE = new QueryColumn(this, "channel_code");

    /**
     * 接收时间
     */
    public final QueryColumn RECEIVE_TIME = new QueryColumn(this, "receive_time");

    /**
     * 短信 API 发送返回的唯一请求 ID
     */
    public final QueryColumn API_REQUEST_ID = new QueryColumn(this, "api_request_id");

    /**
     * 模板编码
     */
    public final QueryColumn TEMPLATE_CODE = new QueryColumn(this, "template_code");

    /**
     * 短信类型
     */
    public final QueryColumn TEMPLATE_TYPE = new QueryColumn(this, "template_type");

    /**
     * API 接收结果的说明
     */
    public final QueryColumn API_RECEIVE_MSG = new QueryColumn(this, "api_receive_msg");

    /**
     * 短信 API 的模板编号
     */
    public final QueryColumn API_TEMPLATE_ID = new QueryColumn(this, "api_template_id");

    /**
     * 接收状态
     */
    public final QueryColumn RECEIVE_STATUS = new QueryColumn(this, "receive_status");

    /**
     * API 接收结果的编码
     */
    public final QueryColumn API_RECEIVE_CODE = new QueryColumn(this, "api_receive_code");

    /**
     * 短信参数
     */
    public final QueryColumn TEMPLATE_PARAMS = new QueryColumn(this, "template_params");

    /**
     * 短信内容
     */
    public final QueryColumn TEMPLATE_CONTENT = new QueryColumn(this, "template_content");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CHANNEL_ID, CHANNEL_CODE, TEMPLATE_ID, TEMPLATE_CODE, TEMPLATE_TYPE, TEMPLATE_CONTENT, TEMPLATE_PARAMS, API_TEMPLATE_ID, MOBILE, USER_ID, USER_TYPE, SEND_STATUS, SEND_TIME, API_SEND_CODE, API_SEND_MSG, API_REQUEST_ID, API_SERIAL_NO, RECEIVE_STATUS, RECEIVE_TIME, API_RECEIVE_CODE, API_RECEIVE_MSG, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemSmsLogTableDef() {
        super("", "system_sms_log");
    }

    private SystemSmsLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemSmsLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemSmsLogTableDef("", "system_sms_log", alias));
    }

}
