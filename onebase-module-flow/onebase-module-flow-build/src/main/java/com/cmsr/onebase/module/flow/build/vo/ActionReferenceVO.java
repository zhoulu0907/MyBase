package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动作引用信息VO
 * <p>
 * 用于返回引用了指定动作的逻辑流节点信息
 *
 * @author onebase
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "动作引用信息")
public class ActionReferenceVO {

    @Schema(description = "逻辑流ID", example = "flow-001")
    private String flowId;

    @Schema(description = "逻辑流名称", example = "用户注册流程")
    private String flowName;

    @Schema(description = "节点ID", example = "node-123")
    private Long nodeId;

    @Schema(description = "节点名称", example = "HTTP请求节点")
    private String nodeName;
}
