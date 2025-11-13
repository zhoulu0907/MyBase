package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "流程代理撤销 VO")
@Data
public class BpmAgentRevokeReqVO {
    @Schema(description = "agentId", requiredMode = Schema.RequiredMode.REQUIRED, example = "143434334")
    @NotNull(message = "ID不能为空")
    private Long agentId;
}
