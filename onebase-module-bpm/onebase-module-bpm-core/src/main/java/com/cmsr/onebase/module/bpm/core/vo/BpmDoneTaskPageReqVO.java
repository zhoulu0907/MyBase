package com.cmsr.onebase.module.bpm.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper = true)
public class BpmDoneTaskPageReqVO extends BpmInsExtQueryPageVO {

    @Schema(description = "发起时间 - 开始（包含）", example = "2025-08-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeStart;

    @Schema(description = "发起时间 - 结束（包含）", example = "2025-08-18 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeEnd;

    @Schema(description = "发起人，支持多个值，格式：1,2,3", example = "3")
    private String initiatorId;

   //========================== 内部使用，前端不用传 ==============
    /**
     * 发起人列表
     */
    private List<String> initiatorIdList;
}