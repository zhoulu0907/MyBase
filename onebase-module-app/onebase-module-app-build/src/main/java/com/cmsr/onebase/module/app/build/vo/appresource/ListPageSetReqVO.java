package com.cmsr.onebase.module.app.build.vo.appresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 查询页面集列表请求VO
 *
 * @author liyang
 * @date 2025-11-11
 */
@Data
public class ListPageSetReqVO {
    /**
     * 应用id
     */
    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "应用id不能为空")
    private Long applicationId;

     /**
     * 页面集类型
     */
    @Schema(description = "页面集类型")
    private Integer pageSetType;
}
