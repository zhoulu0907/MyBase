package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_component")
@Data
@EqualsAndHashCode(callSuper = true)
public class ComponentDO extends TenantBaseDO {

    /**
     * 组件编码
     */
    @Column(name = "component_code", columnDefinition = "VARCHAR(255) NOT NULL", comment = "组件编码")
    private String componentCode;

    /**
     * 页面ID
     */
    @Column(name = "page_id", columnDefinition= "BIGINT NOT NULL", comment = "页面ID")
    private Long pageId;


    /**
     * 是否在子表中
     */
    @Column(name = "in_table", columnDefinition= "BOOLEAN NOT NULL", comment = "是否在子表中")
    private Boolean inTable;

    /**
     * 组件类型
     */
    @Column(name = "component_type", columnDefinition = "VARCHAR(64) NOT NULL", comment = "组件类型")
    private String componentType;

    /**
     * 组件标题
     */
    @Column(name = "label", columnDefinition = "VARCHAR(255) NOT NULL", comment = "组件标题")
    private String label;

    /**
     * 组件宽度
     */
    @Column(name = "width", columnDefinition = "INT NOT NULL", comment = "组件宽度")
    private Integer width;

    /**
     * 是否隐藏
     */
    @Column(name = "hidden", columnDefinition = "BOOLEAN NOT NULL", comment = "是否隐藏")
    private Boolean hidden;

    /**
     * readOnly
     */
    @Column(name = "read_only", columnDefinition = "VARCHAR(255) NOT NULL", comment = "只读绑定（如PageData.isViewMode）")
    private String readOnly;

    /**
     * 是否必填
     */
    @Column(name = "required", columnDefinition = "BOOLEAN NOT NULL", comment = "是否必填")
    private Boolean required;

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

}
