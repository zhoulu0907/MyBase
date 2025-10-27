package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * 应用权限功能定义
 *
 * @Author：huangjie
 * @Date：2025/10/24 14:15
 */
@Data
public class PermissionDTO {

    /**
     * 权限id
     */
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 页面是否可访问
     */
    private Integer isPageAllowed;

    /**
     * 所有视图可访问
     */
    private Integer isAllViewsAllowed;

    /**
     * 所有字段可操作
     */
    private Integer isAllFieldsAllowed;

    /**
     * 操作权限标签
     */
    private List<String> operationTags;

    /**
     * 视图id
     */
    private List<Long> viewIds;

}