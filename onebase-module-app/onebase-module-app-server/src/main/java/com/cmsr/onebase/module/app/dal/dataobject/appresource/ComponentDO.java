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
     *  组件占位符
     */
    @Column(name = "placeholder", columnDefinition = "VARCHAR(255) NOT NULL", comment = "组件占位符")
    private String placeholder;

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


    // TODO(mickey): 补充Data部分

}
