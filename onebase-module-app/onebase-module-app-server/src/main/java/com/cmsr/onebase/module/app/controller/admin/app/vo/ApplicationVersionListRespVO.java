package com.cmsr.onebase.module.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

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
    private String versionName;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String creatorName;

}
