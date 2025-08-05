package com.cmsr.onebase.module.system.dal.dataobject.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典类型表
 *
 * @author ruoyi
 */
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@Table(name = "system_dict_type")
public class DictTypeDO extends BaseDO {

    // 字段名常量
    public static final String NAME         = "name";
    public static final String TYPE         = "type";
    public static final String STATUS       = "status";
    public static final String REMARK       = "remark";
    public static final String DELETED_TIME = "deleted_time";

    /**
     * 字典名称
     */
    @Column(name = NAME)
    private String name;

    /**
     * 字典类型
     */
    @Column(name = TYPE)
    private String type;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;

    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

    /**
     * 删除时间
     */
    @Column(name = DELETED_TIME)
    private LocalDateTime deletedTime;

}
