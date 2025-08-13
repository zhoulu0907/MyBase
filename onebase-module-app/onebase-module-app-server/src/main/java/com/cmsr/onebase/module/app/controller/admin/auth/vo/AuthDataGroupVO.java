package com.cmsr.onebase.module.app.controller.admin.auth.vo;

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

    @Schema(description = "数据权限组编码")
    private String groupCode;

    @Schema(description = "数据权限组排序")
    private Integer groupOrder;

    @Schema(description = "数据权限组描述")
    private String description;

    @Schema(description = "业务实体字段code")
    private String scopeFieldCode;

    @Schema(description = "业务实体字段对应的权限范围")
    private String scopeLevel;

    @Schema(description = "是否可以操作")
    private Boolean operable;

    private List<List<AuthDataFilterVO>> dataFilters;

}
