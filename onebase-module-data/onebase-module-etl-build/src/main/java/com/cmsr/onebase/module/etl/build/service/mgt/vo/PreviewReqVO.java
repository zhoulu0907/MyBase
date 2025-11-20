package com.cmsr.onebase.module.etl.build.service.mgt.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/20 12:22
 */
@Data
@Schema(description = "数据预览请求VO")
public class PreviewReqVO {

    @Schema(description = "流程定义")
    private JsonNode workflow;

    @Schema(description = "节点ID")
    private String nodeId;
}
