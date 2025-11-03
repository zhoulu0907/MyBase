package com.cmsr.onebase.module.bpm.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper = true)
public class BpmDoneTaskPageReqVO extends BpmInsExtQueryPageVO {
    @Schema(description = "处理操作：PASS通过 REJECT退回 NONE无动作")
    private String skipType;

    @Schema(description = "处理时间 - 开始（包含）", example = "2025-08-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime handleTimeStart;

    @Schema(description = "处理时间 - 结束（包含）", example = "2025-08-18 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime handleTimeEnd;

    @Schema(description = "排序方式：desc-最新处理的, asc-最早处理的",
            example = "desc", defaultValue = "desc")
    private String sortType;
}