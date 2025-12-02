package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName GetFormPageListByAppIdReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetFormPageListByAppIdReqVO {
    // TODO: replace with applicationManager?
    @Schema(description = "appId", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "应用id不能为空")
    private Long appId;
}
