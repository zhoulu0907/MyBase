package com.cmsr.onebase.module.system.vo.corpapprelation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "企业应用关联创建/修改 Request VO")
@Data
public class AppAuthTimeReqVO {
    @Schema(description = "应用id")
    @NotNull(message = "授权应用ID不能为空")
    private  Long id;

    @Schema(description = "授权时间")
    @NotNull(message = "授权时间")
    private LocalDateTime authorizationTime ;

    @Schema(description = "过期时间")
    @NotNull(message = "过期时间")
    private LocalDateTime expiresTime ;
}