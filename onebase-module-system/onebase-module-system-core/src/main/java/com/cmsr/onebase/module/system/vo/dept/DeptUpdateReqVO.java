package com.cmsr.onebase.module.system.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Schema(description = "管理后台 - 部门创建/修改 Request VO")
@Data
public class DeptUpdateReqVO {

    @Schema(description = "部门编号", example = "1024")
    private Long id;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
    private String name;

    @Schema(description = "父部门 ID", example = "1024")
    private Long parentId;

    @Schema(description = "主管UserID", example = "2048")
    private Long leaderUserId;

    @Schema(description = " 接口人UserIds", example = "")
    private Set<Long> adminUserIds;

}

