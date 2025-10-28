package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * 应用数据组定义
 * @Author：huangjie
 * @Date：2025/10/24 17:44
 */
@Data
public class DataGroupDTO {

    /**
     * 数据组id
     */
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 组排序
     */
    private Integer groupOrder;

    /**
     * 描述
     */
    private String description;

    /**
     * 权限标签
     */
    private List<String> scopeTags;

    /**
     * 字段id
     */
    private Long scopeFieldId;

    /**
     * 字段对应的权限范围
     */
    private String scopeLevel;

    /**
     * 字段对应的权限范围值
     */
    private String scopeValue;

    /**
     * 操作标签
     */
    private List<String> operationTags;

    /**
     * 数据过滤条件
     */
    private List<List<DataFilterDTO>> dataFilters;
}
