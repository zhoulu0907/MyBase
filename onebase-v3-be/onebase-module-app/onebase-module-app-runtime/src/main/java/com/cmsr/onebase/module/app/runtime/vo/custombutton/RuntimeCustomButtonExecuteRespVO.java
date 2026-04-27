package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运行态-自定义按钮执行响应")
public class RuntimeCustomButtonExecuteRespVO {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "执行日志ID")
    private Long execLogId;

    @Schema(description = "消息")
    private String message;
}
