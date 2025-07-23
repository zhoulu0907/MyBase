package com.cmsr.onebase.module.app.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:48
 */
@Schema(description = "应用管理 - 版本创建 Request VO")
@Data
public class ApplicationVersionCreateReqVO {

    @Schema(description = "应用 ID")
    private Long applicationId;

    @Schema(description = "版本名称")
    private String name;

}
