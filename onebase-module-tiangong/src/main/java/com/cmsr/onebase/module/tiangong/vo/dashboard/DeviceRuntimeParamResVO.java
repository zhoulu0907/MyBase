package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备运行参数 VO
 */
@Schema(description = "设备运行参数响应 VO")
@Data
public class DeviceRuntimeParamResVO {

    @Schema(description = "转速 (F008)")
    private Double speed;

    @Schema(description = "当前电流 (1001)")
    private Double current;

    @Schema(description = "输出功率 (1005)")
    private Double outputPower;

    @Schema(description = "电机额定功率 (F101)")
    private Double ratedPower;

    @Schema(description = "输出电压 (1003)")
    private Double outputVoltage;

    @Schema(description = "输入电压 (1002)")
    private Double inputVoltage;

    @Schema(description = "输出电流 (1004)")
    private Double outputCurrent;

    @Schema(description = "故障码 (8000)")
    private String faultCode;

    @Schema(description = "运行时间 (1018)")
    private Long runTime;

    @Schema(description = "压力 (700B)")
    private Double pressure;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "更新时间")
    private String updateTime;
}