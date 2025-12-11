package com.cmsr.onebase.module.app.core.vo.resource;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePageNameReqVO {

    @NotNull(message = "页面id不能为空")
    private Long id;

    @NotNull(message = "页面名称不能为空")
    private String pageName;
}
