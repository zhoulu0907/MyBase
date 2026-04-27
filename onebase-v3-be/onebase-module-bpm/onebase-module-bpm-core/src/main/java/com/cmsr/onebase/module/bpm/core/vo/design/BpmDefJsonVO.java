package com.cmsr.onebase.module.bpm.core.vo.design;

import com.cmsr.onebase.module.bpm.core.vo.design.edge.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
    @Valid
    @NotEmpty(message = "流程节点信息不能为空")
    private List<BaseNodeVO> nodes;

    /**
     * 边信息
     */
    @Valid
    @NotEmpty(message = "流程边信息不能为空")
    private List<BaseEdgeVO> edges;
}