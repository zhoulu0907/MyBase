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
public class BpmDesignSaveReqVO {
    /**
     * 保存必填字段
     * 新增可以为空
     */
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "流程编码")
    private String flowCode;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "流程版本备注")
    private String bpmVersionAlias;

    /**
     * 业务UUID，用于关联业务系统的业务数据
     *
     */
    @NotBlank(message = "业务不能为空")
    @Schema(description = "业务ID，当前菜单UUID")
    private String businessId;

    /**
     * 流程定义JSON
     */
    @Schema(description = "流程定义JSON")
    @NotBlank(message = "流程定义JSON不能为空")
    private String bpmDefJson;

    /**
     * 全局配置
     */
    @Schema(description = "全局配置")
    private BpmGlobalConfigDTO globalConfig = new BpmGlobalConfigDTO();

    /**
     * 流程定义JSONVO
     *
     * 不返回给前端展示，用于解析和校验bpmDefJson后存储使用
     *
     */
    @Schema(description = "流程定义JSONVO")
    private BpmDefJsonVO bpmDefJsonVO;
}