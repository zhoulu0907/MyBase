package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/24 17:44
 */
@Data
@Schema(description = "应用数据组定义")
public class DataGroupDTO {

    @Schema(description = "数据组id")
    private Long id;

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "组名称")
    private String groupName;

    @Schema(description = "组排序")
    private Integer groupOrder;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "权限标签")
    private String scopeTags;

    @Schema(description = "字段id")
    private Long scopeFieldId;

    @Schema(description = "字段对应的权限范围")
    private String scopeLevel;

    @Schema(description = "字段对应的权限范围值")
    private String scopeValue;

    @Schema(description = "操作标签")
    private List<String> operationTags;

    @Schema(description = "数据过滤条件")
    private List<DataFilterDTO> dataFilters;
}
