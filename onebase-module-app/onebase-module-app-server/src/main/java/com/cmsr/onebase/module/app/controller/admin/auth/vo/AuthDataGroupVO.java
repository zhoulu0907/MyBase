package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 14:51
 */
@Data
@Schema(description = "应用管理 - 数据 Request VO")
public class AuthDataGroupVO {

    @Schema(description = "数据权限组名称")
    private String groupName;

    @Schema(description = "数据权限组描述")
    private String description;

    @Schema(description = "关联业务实体Id")
    private Long entityId;

    @Schema(description = "关联业务实体名称")
    private Long entityName;

    @Schema(description = "关联业务实体字段Id")
    private Long scopeFieldId;

    @Schema(description = "关联业务实体字段对应的权限范围")
    private String scopeLevel;

    @Schema(description = "是否可以操作")
    private Boolean isOperable;
}
