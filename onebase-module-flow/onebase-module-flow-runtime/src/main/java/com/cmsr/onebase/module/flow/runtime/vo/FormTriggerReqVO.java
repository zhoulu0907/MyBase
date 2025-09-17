package com.cmsr.onebase.module.flow.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:57
 */
@Data
@Schema(description = "表单触发请求VO")
public class FormTriggerReqVO {

    @Schema(description = "流程定义ID")
    private Long processId;

    private String executionUuid;

    @Schema(description = "输入参数")
    private Map<String, String> inputParams;

}
