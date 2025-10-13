package com.cmsr.onebase.module.flow.runtime.vo;

import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "表单触发响应VO")
public class FormTriggerRespVO {

    @Schema(description = "是否触发了执行")
    private Boolean triggered;

    private boolean success;

    private String code;

    private String message;

    private String cause;

    private boolean executionEnd;

    private String executionUuid;

    private Map<String, Object> outputParams;

}
