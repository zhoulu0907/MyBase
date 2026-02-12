package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备告警信息 VO")
@Data
public class DeviceAlarmVO {

    @Schema(description = "告警ID")
    private Integer id;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "告警名称")
    private String alarmName;

    @Schema(description = "告警时间")
    private String alarmTime;

}