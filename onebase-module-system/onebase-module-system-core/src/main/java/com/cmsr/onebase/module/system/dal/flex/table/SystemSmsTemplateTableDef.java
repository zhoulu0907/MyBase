package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 短信模板 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemSmsTemplateTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 短信模板
     */
    public static final SystemSmsTemplateTableDef SYSTEM_SMS_TEMPLATE = new SystemSmsTemplateTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 模板编码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 模板名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 模板类型
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

    /**
     * 参数数组
     */
    public final QueryColumn PARAMS = new QueryColumn(this, "params");

    /**
     * 备注
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态（0停用，1启用）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 模板内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

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
     * 短信渠道编号
     */
    public final QueryColumn CHANNEL_ID = new QueryColumn(this, "channel_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 短信渠道编码
     */
    public final QueryColumn CHANNEL_CODE = new QueryColumn(this, "channel_code");

    /**
     * 短信 API 的模板编号
     */
    public final QueryColumn API_TEMPLATE_ID = new QueryColumn(this, "api_template_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TYPE, STATUS, CODE, NAME, CONTENT, PARAMS, REMARK, API_TEMPLATE_ID, CHANNEL_ID, CHANNEL_CODE, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemSmsTemplateTableDef() {
        super("", "system_sms_template");
    }

    private SystemSmsTemplateTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemSmsTemplateTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemSmsTemplateTableDef("", "system_sms_template", alias));
    }

}
