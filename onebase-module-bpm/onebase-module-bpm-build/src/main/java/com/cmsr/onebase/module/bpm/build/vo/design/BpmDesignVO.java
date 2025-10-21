package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程设计结构视图
 *
 * 在 WarmFlow DefJson 基础上修改，添加了扩展字段和转换信息，便于前端展示。
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程信息请求VO")
public class BpmDesignVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程编码")
    private String flowCode;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "流程版本")
    private String version;

    @Schema(description = "流程版本备注")
    private String versionAlias;

    @Schema(description = "是否发布（0未发布 1已发布 9失效）")
    private Integer isPublish = 0;

    /**
     * 业务ID，用于关联业务系统的业务数据
     *
     * 通常为表单ID
     */
    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "流程激活状态（0挂起 1激活）")
    private Integer activityStatus = 1;

    /**
     * 节点信息
     */
    private List<BpmNodeVO> nodeList;
}