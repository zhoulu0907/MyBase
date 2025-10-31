package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程预览VO
 */
@Schema(description = "流程预览VO")
@Data
public class BpmFlowPreviewVO {
    @Schema(description = "流程节点编码")
    private String nodeCode;

    @Schema(description = "流程节点名称")
    private String nodeName;

    @Schema(description = "处理人信息")
    private List<HandlerInfo> handlers;

    @Schema(description = "是否是当前节点")
    private boolean currentNode ;

    @Schema(description = "是否是开始节点")
    @Data
    public static class HandlerInfo {
        @Schema(description = "处理人ID")
        private Long userId;

        @Schema(description = "处理人名称")
        private String userName;

        @Schema(description = "处理人头像")
        private String userAvatar;
    }
}
