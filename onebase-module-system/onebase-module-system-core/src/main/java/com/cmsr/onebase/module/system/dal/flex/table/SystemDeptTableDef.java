package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 部门表 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemDeptTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 部门表
     */
    public static final SystemDeptTableDef SYSTEM_DEPT = new SystemDeptTableDef();

    /**
     * 部门id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 部门名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 显示顺序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 邮箱
     */
    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    /**
     * 联系电话
     */
    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    /**
     * 企业ID
     */
    public final QueryColumn CORP_ID = new QueryColumn(this, "corp_id");

    /**
     * 简介和备注
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态（0停用，1启用）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 部门编码（第三方dept_third_default）
     */
    public final QueryColumn DEPT_CODE = new QueryColumn(this, "dept_code");

    /**
     * 枚举tenant、corp
     */
    public final QueryColumn DEPT_TYPE = new QueryColumn(this, "dept_type");

    /**
     * 父部门id
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 部门管理员
     */
    public final QueryColumn ADMIN_USER_ID = new QueryColumn(this, "admin_user_id");

    /**
     * 部门主管
     */
    public final QueryColumn LEADER_USER_ID = new QueryColumn(this, "leader_user_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PARENT_ID, SORT, LEADER_USER_ID, PHONE, EMAIL, STATUS, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, TENANT_ID, DELETED, REMARK, ADMIN_USER_ID, DEPT_TYPE, CORP_ID, DEPT_CODE};

    public SystemDeptTableDef() {
        super("", "system_dept");
    }

    private SystemDeptTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemDeptTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemDeptTableDef("", "system_dept", alias));
    }

}
