package com.cmsr.onebase.module.flow.runtime.vo;

import com.cmsr.onebase.module.flow.context.graph.nodes.ModalNodeData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
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

    @Schema(description = "执行ID，对于交互式流程设置为后端返回的值")
    private String executionUuid;

    @Schema(description = "输入参数，表单数据，用于条件过滤，表单信息，用于前端提交数据的时候调用")
    private Map<String, Object> inputParams;

    @Schema(description = "输入字段，表单字段，表单数据收集，用于flow流程弹窗再次回传")
    private List<ModalNodeData.Field> inputFields;

}
