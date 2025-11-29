package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程预测响应VO
 *
 * @author liyang
 * @date 2025-11-04
 */
@Schema(description = "流程预测VO")
@Data
public class BpmPredictRespVO {
    private List<NodeInfo> nodes;

    @Data
    public static class NodeInfo {
        @Schema(description = "流程节点编码")
        private String nodeCode;

        @Schema(description = "流程节点名称")
        private String nodeName;

        @Schema(description = "处理人信息")
        private List<HandlerInfo> handlers;

        @Schema(description = "是否是当前节点")
        private boolean currentNode ;
    }

    @Schema(description = "处理人信息")
    @Data
    public static class HandlerInfo {
        @Schema(description = "处理人ID")
        private String handlerId;

        @Schema(description = "处理人名称")
        private String handlerName;

        @Schema(description = "处理人头像")
        private String avatar;
    }
}
