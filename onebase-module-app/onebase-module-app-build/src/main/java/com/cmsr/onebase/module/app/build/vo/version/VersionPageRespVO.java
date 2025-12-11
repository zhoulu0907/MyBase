package com.cmsr.onebase.module.app.build.vo.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:43
 */
@Schema(description = "应用管理 - 版本列表 Response VO")
@Data
public class VersionPageRespVO {

    @Schema(description = "版本 ID")
    private Long id;

    @Schema(description = "应用 ID")
    private Long applicationId;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "版本描述")
    private String versionDescription;

    @Schema(description = "版本类型")
    private Integer versionType;

    @Schema(description = "版本类型中文描述")
    private String versionTypeLabel;

    @Schema(description = "操作类型")
    private Integer operationType;

    @Schema(description = "环境")
    private String environment;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updaterName;

}
