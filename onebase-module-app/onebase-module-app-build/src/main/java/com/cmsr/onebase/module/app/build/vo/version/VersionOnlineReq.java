package com.cmsr.onebase.module.app.build.vo.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 16:48
 */
@Schema(description = "应用管理 - 版本创建 Request VO")
@Data
public class VersionOnlineReq {

    @Schema(description = "应用 ID")
    private Long applicationId;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "版本描述")
    private String versionDescription;

    @Schema(description = "环境")
    private String environment;

    @Schema(description = "操作类型")
    private Integer operationType;
}
