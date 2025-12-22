package com.cmsr.onebase.module.system.dal.dataobject.dept;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 部门表
 *
 * @author ma
 */
@Table(value = "system_dept")
@Data
public class DeptDO extends BaseTenantEntity {

    public static final Long PARENT_ID_ROOT = 0L;

    // 列名常量
    public static final String NAME           = "name";
    public static final String PARENT_ID      = "parent_id";
    public static final String SORT           = "sort";
    public static final String LEADER_USER_ID = "leader_user_id";
    public static final String PHONE          = "phone";
    public static final String EMAIL          = "email";
    public static final String STATUS         = "status";
    public static final String REMARK         = "remark";
    public static final String ADMIN_USER_ID  = "admin_user_id";
    public static final String CORP_ID        = "corp_id";
    public static final String DEPT_TYPE      = "dept_type";
    public static final String DEPT_CODE      = "dept_code";

    /**
     * 部门名称
     */
    @Column(value = NAME)
    private String  name;
    /**
     * 父部门ID
     * <p>
     */
    @Column(value = PARENT_ID)
    private Long    parentId;
    /**
     * 显示顺序
     */
    @Column(value = SORT)
    private Integer sort;
    /**
     * 主管UserID
     * <p>
     * 关联
     */
    @Column(value = LEADER_USER_ID)
    private Long    leaderUserId;

    /**
     * 管理员id
     */
    @Column(value = ADMIN_USER_ID)
    private Long adminUserId;

    /**
     * 联系电话
     */
    @Column(value = PHONE)
    private String  phone;
    /**
     * 邮箱
     */
    @Column(value = EMAIL)
    private String  email;
    /**
     * 部门状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;

    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;


    /**
     * 部门类型： tenant-空间部门，corp-企业部门
     */
    @Column(value = DEPT_TYPE)
    private String deptType;

    /**
     * 归属企业ID
     */
    @Column(value = CORP_ID)
    private Long corpId;

    @Column(value = DEPT_CODE)
    private String deptCode;

}
