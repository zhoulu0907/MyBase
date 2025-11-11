package com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "app_resource_workbench_component")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchComponentDO extends TenantBaseDO {
    public static final String PAGE_ID = "page_id";
    public static final String COMPONENT_INDEX = "component_index";

    /**
     * 组件编码
     */
    @Column(name = "component_code", columnDefinition = "VARCHAR(255) NOT NULL", comment = "组件编码")
    private String componentCode;

    /**
     * 页面编码
     */
    @Column(name = "page_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面ID")
    private Long pageId;

    /**
     * 组件类型
     */
    @Column(name = "component_type", columnDefinition = "VARCHAR(64) NOT NULL", comment = "组件类型")
    private String componentType;

    /**
     * 配置
     */
    @Column(name = "config", columnDefinition = "TEXT NOT NULL", comment = "配置")
    private String config;

    /**
     * 编辑数据
     */
    @Column(name = "edit_data", columnDefinition = "TEXT NOT NULL", comment = "编辑数据")
    private String editData;

    @Column(name = "parent_code", columnDefinition = "VARCHAR(255)", comment = "父组件编码")
    private String parentCode;

    /**
     * 块索引
     */
    @Column(name = "block_index", columnDefinition = "INT8 NOT NULL DEFAULT 0", comment = "块索引")
    private Integer blockIndex;

    /**
     * 容器索引
     */
    @Column(name = "container_index", columnDefinition = "INT8 NOT NULL DEFAULT 0", comment = "容器索引")
    private Integer containerIndex;

    /**
     * 组件索引
     */
    @Column(name = "component_index", columnDefinition = "INT8 NOT NULL DEFAULT 0", comment = "组件索引")
    private Integer componentIndex;

}
