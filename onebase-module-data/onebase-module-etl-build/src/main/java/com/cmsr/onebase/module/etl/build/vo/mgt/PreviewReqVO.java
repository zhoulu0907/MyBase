package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/20 12:22
 */
@Schema(description = "数据工厂 - Etl - 数据预览请求VO")
@Data
public class PreviewReqVO {

    @Schema(description = "流程定义")
    private JsonNode workflow;

    @Schema(description = "节点ID")
    private String nodeId;
}
