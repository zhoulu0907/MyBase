package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

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
    private Integer isCanRead = NumberUtils.INTEGER_ONE;

    @Schema(description = "是否可编辑")
    private Integer isCanEdit = NumberUtils.INTEGER_ZERO;

    @Schema(description = "是否可下载")
    private Integer isCanDownload = NumberUtils.INTEGER_ZERO;

}
