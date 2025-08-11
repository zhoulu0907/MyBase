package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

/**
 * 角色 DO
 *
 * @author ruoyi
 */
@Data
@Table(name = "system_role")
public class RoleDO extends TenantBaseDO {

    // 字段常量
    public static final String NAME                = "name";
    public static final String CODE                = "code";
    public static final String SORT                = "sort";
    public static final String STATUS              = "status";
    public static final String TYPE                = "type";
    public static final String REMARK              = "remark";
    public static final String DATA_SCOPE          = "data_scope";
    public static final String DATA_SCOPE_DEPT_IDS = "data_scope_dept_ids";

    // builder模式可正常运作
    // public RoleDO setId(Long id){
    //     super.setId(id);
    //     return this;
    // }

    /**
     * 角色名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 角色标识
     *
     * 枚举
     */
    @Column(name = CODE)
    private String code;
    /**
     * 角色排序
     */
    @Column(name = SORT)
    private Integer sort;
    /**
     * 角色状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 角色类型
     *
     * 枚举 {@link RoleTypeEnum}
     */
    @Column(name = TYPE)
    private Integer type;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

    /**
     * 数据���围
     *
     * 枚举 {@link DataScopeEnum}
     */
    @Column(name = DATA_SCOPE)
    private Integer dataScope;
    /**
     * 数据范围(指定部门数组)
     *
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    @Column(name = DATA_SCOPE_DEPT_IDS, columnDefinition="json")
    // 添加类型转换注解，确保Set<Long>正确映射
    private Set<Long> dataScopeDeptIds;

}
