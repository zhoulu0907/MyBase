package com.cmsr.onebase.module.bpm.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "我创建的流程 - 分页列表请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class BpmMyCreatedPageReqVO extends BpmInsExtQueryPageVO {
    @Schema(description = "创建时间 - 开始（包含）", example = "2025-08-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间 - 结束（包含）", example = "2025-08-18 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeEnd;
}
