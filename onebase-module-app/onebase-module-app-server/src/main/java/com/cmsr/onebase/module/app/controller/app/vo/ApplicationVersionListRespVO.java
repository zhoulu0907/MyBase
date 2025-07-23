package com.cmsr.onebase.module.app.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:43
 */
@Schema(description = "应用管理 - 版本列表 Response VO")
@Data
public class ApplicationVersionListRespVO {

    @Schema(description = "版本 ID")
    private Long id;

    @Schema(description = "应用 ID")
    private Long applicationId;

    @Schema(description = "版本名称")
    private String name;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "更新人")
    private String updateUser;

}
