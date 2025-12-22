package com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全配置分类表
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_security_config_category")
@TenantIgnore
public class SecurityConfigCategoryDO extends BaseDO {

    public static final String CATEGORY_CODE = "category_code";
    public static final String CATEGORY_NAME = "category_name";
    public static final String DESCRIPTION = "description";
    public static final String SORT_ORDER = "sort_order";

    /**
     * 分类编码
     */
    @Column(value = CATEGORY_CODE)
    private String categoryCode;

    /**
     * 分类名称
     */
    @Column(value = CATEGORY_NAME)
    private String categoryName;

    /**
     * 分类描述
     */
    @Column(value = DESCRIPTION)
    private String description;

    /**
     * 排序号
     */
    @Column(value = SORT_ORDER)
    private Integer sortOrder;

}
