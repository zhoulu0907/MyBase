package com.cmsr.onebase.plugin.simulator.controller.plugin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 批量操作结果 Response VO
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Schema(description = "插件管理 - 批量操作结果 Response VO")
@Data
public class PluginBatchOperationRespVO {

    @Schema(description = "总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer total;

    @Schema(description = "成功数", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Integer successCount;

    @Schema(description = "失败数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer failureCount;

    @Schema(description = "详细结果列表")
    private List<PluginOperationResultItemVO> items;

    public PluginBatchOperationRespVO(List<PluginOperationResultItemVO> items) {
        this.items = items;
        this.total = items.size();
        this.successCount = (int) items.stream().filter(PluginOperationResultItemVO::getSuccess).count();
        this.failureCount = total - successCount;
    }
}
