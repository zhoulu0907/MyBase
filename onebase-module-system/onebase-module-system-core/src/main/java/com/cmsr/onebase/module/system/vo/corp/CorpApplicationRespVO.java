package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CorpApplicationRespVO {

    @Schema(description = "Id")
    private Long id;
    @Schema(description = "应用名称")
    private String applicationName;
    @Schema(description = "应用uid")
    private String applicationUid;
    @Schema(description = "应用编码")
    private String applicationCode;
    @Schema(description = "应用Id")
    private Long applicationId;
    @Schema(description = "授权时间")
    private LocalDateTime authorizationTime ;
    @Schema(description = "版本号")
    private String versionNumber;
    @Schema(description = "过期时间")
    private LocalDateTime expiresTime;
    @Schema(description = "状态值")
    private Integer    showStatus;

}
