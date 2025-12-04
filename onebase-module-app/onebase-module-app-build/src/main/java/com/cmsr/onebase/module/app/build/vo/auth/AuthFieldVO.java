package com.cmsr.onebase.module.app.build.vo.auth;

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

    @Schema(description = "字段uuid")
    private String fieldUuid;

    @Schema(description = "字段名称")
    private String fieldDisplayName;

    @Schema(description = "是否可阅读")
    private Integer isCanRead;

    @Schema(description = "是否可编辑")
    private Integer isCanEdit;

    @Schema(description = "是否可下载")
    private Integer isCanDownload;

    @Schema(description = "字段类型", example = "STRING")
    private String fieldType;

}
