package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 14:48
 */
@Data
@Schema(description = "应用管理 - 实体 Response VO")
public class AuthEntityVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "实体Id")
    private Long entityId;

    @Schema(description = "是否可访问")
    private Boolean allowed;

}
