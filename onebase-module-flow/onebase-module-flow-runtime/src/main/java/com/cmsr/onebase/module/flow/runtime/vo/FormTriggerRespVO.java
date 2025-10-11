package com.cmsr.onebase.module.flow.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "表单触发响应VO")
public class FormTriggerRespVO {

    @Schema(description = "执行ID，用于交互式执行中恢复需要恢复执行标志")
    private String executionUuid;

    @Schema(description = "是否触发了执行")
    private Integer triggered;

    @Schema(description = "执行结果")
    private Map<String, Object> result;

}
