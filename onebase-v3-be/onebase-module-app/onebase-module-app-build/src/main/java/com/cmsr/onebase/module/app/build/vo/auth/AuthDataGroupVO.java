package com.cmsr.onebase.module.app.build.vo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 14:51
 */
@Data
@Schema(description = "应用管理 - 数据 Request VO")
public class AuthDataGroupVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "数据权限组名称")
    private String groupName;

    @Schema(description = "数据权限组排序")
    private Integer groupOrder;

    @Schema(description = "数据权限组描述")
    private String description;

    @Schema(description = "权限范围标签")
    private List<String> scopeTags;

    @Schema(description = "权限范围字段id")
    private String scopeFieldUuid;

    @Schema(description = "权限范围")
    private String scopeLevel;

    @Schema(description = "权限范围值")
    private String scopeValue;

    @Schema(description = "操作标签")
    private List<String> operationTags;

    @Schema(description = "数据权限组对应的数据过滤条件")
    private List<List<AuthDataFilterVO>> dataFilters;

}
