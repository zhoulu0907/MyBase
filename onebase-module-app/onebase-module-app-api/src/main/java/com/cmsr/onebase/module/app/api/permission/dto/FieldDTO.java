package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 17:56
 */
@Data
@Schema(description = "应用字段权限定义")
public class FieldDTO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "字段id")
    private Long fieldId;

    @Schema(description = "是否可阅读")
    private Integer isCanRead;

    @Schema(description = "是否可编辑")
    private Integer isCanEdit;

    @Schema(description = "是否可下载")
    private Integer isCanDownload;

}
