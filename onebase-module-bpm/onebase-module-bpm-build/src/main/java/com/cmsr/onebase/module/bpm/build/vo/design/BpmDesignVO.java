package com.cmsr.onebase.module.bpm.build.vo.design;

import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "流程信息请求VO")
public class BpmDesignVO extends BpmDefinitionVO {
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