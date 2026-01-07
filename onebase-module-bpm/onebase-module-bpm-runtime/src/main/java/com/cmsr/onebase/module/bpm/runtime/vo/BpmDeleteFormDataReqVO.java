package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除表单数据请求VO
 *
 * @author liyang
 * @date 2025-12-17
 */
@Data
public class BpmDeleteFormDataReqVO {
    @Schema(description = "实体表名", example = "sog_main")
    @NotBlank(message = "实体表名不能为空")
    private String tableName;

    @NotNull(message = "菜单ID不能为空")
    @Schema(description = "菜单ID", example = "1111")
    private Long menuId;

    @Schema(description = "数据ID", example = "1111")
    @NotNull(message = "数据ID不能为空")
    private Long id;
}
