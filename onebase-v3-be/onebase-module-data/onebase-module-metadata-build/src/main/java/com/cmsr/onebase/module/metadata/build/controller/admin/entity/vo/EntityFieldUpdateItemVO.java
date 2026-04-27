package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 字段更新项 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段更新项 VO")
@Data
public class EntityFieldUpdateItemVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    @NotNull(message = "字段ID不能为空")
    private String id;

    @Schema(description = "显示名称", example = "登录用户名")
    @Size(max = 50, message = "显示名称长度不能超过50个字符")
    private String displayName;

    @Schema(description = "描述", example = "系统登录使用的用户名")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "是否必填：0-是，1-不是", example = "0")
    private Integer isRequired;


}
