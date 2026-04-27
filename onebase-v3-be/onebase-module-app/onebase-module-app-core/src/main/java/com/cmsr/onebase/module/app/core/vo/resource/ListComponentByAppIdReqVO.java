package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 根据应用ID查询 list 类型页面下组件列表请求 VO
 *
 * @author mickey
 * @date 2025-02-05
 */
@Data
public class ListComponentByAppIdReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

}
