package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 17:56
 */
@Data
@Schema(description = "应用管理 - 字段权限 Response VO")
public class AuthFieldVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "字段id")
    private Long fieldId;

    @Schema(description = "字段名称")
    private String fieldDisplayName;

    @Schema(description = "是否可阅读")
    private Boolean canRead = Boolean.TRUE;

    @Schema(description = "是否可编辑")
    private Boolean canEdit = Boolean.TRUE;

    @Schema(description = "是否可下载")
    private Boolean canDownload = Boolean.TRUE;

}
