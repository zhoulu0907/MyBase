package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePageNameReqVO {

    @NotNull(message = "页面编码不能为空")
    private String pageCode;

    @NotNull(message = "页面名称不能为空")
    private String pageName;
}
