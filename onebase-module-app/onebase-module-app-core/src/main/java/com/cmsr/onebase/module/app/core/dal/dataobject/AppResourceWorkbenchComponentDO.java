package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "app_resource_workbench_component")
public class AppResourceWorkbenchComponentDO extends BaseTenantEntity {


    /**
     * 组件编码
     */
    @Column(value = "component_code", comment = "组件编码")
    private String componentCode;

    /**
     * 页面编码
     */
    @Column(value = "page_id", comment = "页面ID")
    private Long pageId;

    /**
     * 组件类型
     */
    @Column(value = "component_type", comment = "组件类型")
    private String componentType;

    /**
     * 配置
     */
    @Column(value = "config", comment = "配置")
    private String config;

    /**
     * 编辑数据
     */
    @Column(value = "edit_data", comment = "编辑数据")
    private String editData;

    @Column(value = "parent_code", comment = "父组件编码")
    private String parentCode;

    /**
     * 块索引
     */
    @Column(value = "block_index", comment = "块索引")
    private Integer blockIndex;

    /**
     * 容器索引
     */
    @Column(value = "container_index", comment = "容器索引")
    private Integer containerIndex;

    /**
     * 组件索引
     */
    @Column(value = "component_index", comment = "组件索引")
    private Integer componentIndex;

}
