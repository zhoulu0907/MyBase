package com.cmsr.onebase.module.bpm.build.vo.design;

import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程设计结构视图
 *
 * 在 WarmFlow DefJson 基础上修改，添加了扩展字段和转换信息，便于前端展示。
 *
 * 只包含节点和边的信息，不包含流程信息。
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程结构VO")
public class BpmDefJsonVO {
    /**
     * 节点信息
     */
    private List<BaseNodeVO> nodes;

    /**
     * 边信息
     */
    private List<EdgeVO> edges;

    @Schema(description = "边信息")
    @Data
    public static class EdgeVO {
        /**
         * 源节点编码
         */
        @Schema(description = "当前流程节点的编码")
        private String sourceNodeId;

        /**
         * 目标节点编码
         */
        @Schema(description = "下一个流程节点的编码")
        private String targetNodeId;

        @Schema(description = "边名称")
        private String name;

        @Schema(description = "边类型（PASS审批通过 REJECT退回）")
        private String type;

        @Schema(description = "条件")
        private String skipCondition;
    }
}