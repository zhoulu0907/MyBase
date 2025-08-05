package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import java.util.List;

import com.cmsr.onebase.module.app.api.appresource.dto.PageDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavePageSetReqVO {

    @Schema(description = "页面集编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面集编码不能为空")
    private String pageSetCode;

    @Schema(description = "页面集名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面集名称不能为空")
    private String pageSetName;

    @Schema(description = "页面集描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "页面集不能为空")
    private List<PageDTO> pages;
}
