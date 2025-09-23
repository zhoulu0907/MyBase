package com.cmsr.onebase.module.app.build.vo.auth;

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

    @Schema(description = "业务实体Id")
    private Long entityId;

    @Schema(description = "业务实体名称")
    private String entityName;

    @Schema(description = "业务实体字段名称")
    private Long scopeFieldId;

    @Schema(description = "业务实体字段对应的权限范围")
    private String scopeLevel;

    @Schema(description = "业务实体字段对应的权限范围值")
    private String scopeValue;

    @Schema(description = "是否可以操作")
    private Integer isOperable;

    private List<List<AuthDataFilterVO>> dataFilters;

}
