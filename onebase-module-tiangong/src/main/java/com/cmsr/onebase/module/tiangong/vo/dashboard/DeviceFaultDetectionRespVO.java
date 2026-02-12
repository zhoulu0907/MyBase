package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备故障检测统计 VO")
@Data
public class DeviceFaultDetectionRespVO {

    @Schema(description = "实时告警数量")
    private Integer realTimeAlarms;

    @Schema(description = "历史告警数量")
    private Integer historicalAlarms;

}