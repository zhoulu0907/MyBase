package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "运行态-自定义按钮执行日志详情")
public class RuntimeCustomButtonExecLogRespVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "按钮编码")
    private String buttonCode;

    @Schema(description = "按钮名称")
    private String buttonName;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "执行状态")
    private String execStatus;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "耗时")
    private Long durationMs;

    @Schema(description = "明细")
    private List<Detail> details;

    @Data
    public static class Detail {
        @Schema(description = "记录ID")
        private String recordId;
        @Schema(description = "执行状态")
        private String execStatus;
        @Schema(description = "错误码")
        private String errorCode;
        @Schema(description = "错误信息")
        private String errorMessage;
    }
}
