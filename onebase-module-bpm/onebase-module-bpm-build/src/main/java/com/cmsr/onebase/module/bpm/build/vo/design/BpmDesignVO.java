package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String bpmDefJson;
}