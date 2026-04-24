package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运行态-自定义按钮批量执行响应")
public class RuntimeCustomButtonBatchExecuteRespVO {

    @Schema(description = "主日志ID")
    private Long execLogId;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "成功条数")
    private Integer successCount;

    @Schema(description = "失败条数")
    private Integer failCount;
}
