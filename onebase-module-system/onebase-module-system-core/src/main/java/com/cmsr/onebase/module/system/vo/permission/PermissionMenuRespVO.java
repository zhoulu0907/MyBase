package com.cmsr.onebase.module.system.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 权限信息 Response VO")
@Data
public class PermissionMenuRespVO {

    @Schema(description = "权限编号",  example = "1024")
    private Long id;

    @Schema(description = "权限名称",  example = "onebase")
    private String name;

    @Schema(description = "权限标识,仅菜单类型为按钮时，才需要传递", example = "sys:menu:add")
    private String permission;

    @Schema(description = "类型，参见 MenuTypeEnum 枚举类",  example = "1")
    private Integer type;

    @Schema(description = "显示顺序",  example = "1024")
    private Integer sort;

    @Schema(description = "状态,见 CommonStatusEnum 枚举",  example = "1")
    private Integer status;

    @Schema(description = "创建时间",  example = "时间戳格式")
    private LocalDateTime createTime;
    /**
     * 父菜单ID
     */
    @Schema(description = "父级ID",  example = "时间戳格式")
    private Long parentId;
}
