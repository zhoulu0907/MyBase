package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "自动化工作流 - 更新脚本连接器动作请求VO")
@Data
public class UpdateFlowConnectorScriptReqVO {

    @Schema(description = "脚本ID")
    @NotNull(message = "脚本ID不能为空")
    private Long scriptId;

    @Schema(description = "脚本名称")
    @NotBlank(message = "脚本名称不能为空")
    private String scriptName;

    @Schema(description = "脚本类型")
    private String scriptType;

    @Schema(description = "脚本描述")
    private String description;

    @Schema(description = "脚本内容")
    @NotBlank(message = "脚本内容不能为空")
    private String rawScript;

    @Schema(description = "输入参数配置")
    private JsonNode inputParameter;

    @Schema(description = "输出参数配置")
    private JsonNode outputParameter;

    public String getInputParameter() {
        if (this.inputParameter == null || this.inputParameter instanceof NullNode) {
            return null;
        }
        if (this.inputParameter instanceof TextNode) {
            return this.inputParameter.asText();
        }
        return JsonUtils.toJsonString(this.inputParameter);
    }

    public String getOutputParameter() {
        if (this.outputParameter == null || this.outputParameter instanceof NullNode) {
            return null;
        }
        if (this.outputParameter instanceof TextNode) {
            return this.outputParameter.asText();
        }
        return JsonUtils.toJsonString(this.outputParameter);
    }
}
