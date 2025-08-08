package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 17:04
 */
@Data
@Schema(description = "应用管理 - 操作权限 Response VO")
public class AuthOperationVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "操作名称")
    private String operationType;

    @Schema(description = "是否允许")
    private Boolean allowed;

}
