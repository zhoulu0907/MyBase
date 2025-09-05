package com.cmsr.onebase.module.flow.controller.admin.exec.vo;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:57
 */
@Data
@Schema(description = "表单触发请求VO")
public class FormTriggerReqVO {

    @Schema(description = "流程定义ID")
    private Long processId;

    @Schema(description = "输入参数")
    private List<InputParam> inputParams;

}
