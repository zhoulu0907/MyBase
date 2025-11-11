package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "流程代理创建 VO")
@Data
public class BpmDelegationInsertReqVO {
    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "代理人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "代理人ID不能为空")
    private Long delegateId;

    @Schema(description = "代理人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "代理人名称不能为空")
    private String delegateName;

    @Schema(description = "代理开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-10-29")
    @NotNull(message = "代理开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "代理结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-10-29")
    @NotNull(message = "代理结束时间不能为空")
    private LocalDateTime endTime;
}
