package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName PageMetadataDO
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/30 14:51
 */
@Table(name="app_page_component")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageComponentDO extends TenantBaseDO {
    /**
     * 页面ID
     */
    @Column(name = "page_id", columnDefinition= "BIGINT NOT NULL", comment = "页面ID")
    private Long pageId;

    /**
     * 关联静态组件
     */
    @Column(name = "component_ref", columnDefinition= "VARCHAR(255) NOT NULL", comment = "关联静态组件")
    private String componentRef;

    /**
     * 是否在子表中
     */
    @Column(name = "in_table", columnDefinition= "BOOLEAN NOT NULL", comment = "是否在子表中")
    private Boolean inTable;

}
