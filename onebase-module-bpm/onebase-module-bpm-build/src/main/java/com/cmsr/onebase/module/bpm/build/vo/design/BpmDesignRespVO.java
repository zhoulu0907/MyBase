package com.cmsr.onebase.module.bpm.build.vo.design;

import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程设计结构视图
 *
 * 在 WarmFlow DefJson 基础上修改，添加了扩展字段和转换信息，便于前端展示。
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程信息保存请求VO")
public class BpmDesignRespVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程编码")
    private String flowCode;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "流程版本")
    private String bpmVersion;

    @Schema(description = "流程版本备注")
    private String bpmVersionAlias;

    @Schema(description = "版本状态")
    private String bpmVersionStatus;

    /**
     * 业务ID，用于关联业务系统的业务数据
     *
     * 通常为表单ID
     */
    @Deprecated
    @Schema(description = "业务ID")
    private Long businessId;

    /**
     *
     * 业务UUID，用于关联业务系统的业务数据
     *
     * 通常为表单UUID
     *
     */
    @Schema(description = "菜单UUID")
    private String menuUuid;

    /**
     * 流程定义JSON
     */
    @Schema(description = "流程定义JSON")
    private String bpmDefJson;

    /**
     * 全局配置
     */
    @Schema(description = "全局配置")
    private BpmGlobalConfigDTO globalConfig = new BpmGlobalConfigDTO();
}