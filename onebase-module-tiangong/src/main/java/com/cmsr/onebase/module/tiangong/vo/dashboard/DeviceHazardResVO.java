package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备隐患信息 VO")
@Data
public class DeviceHazardResVO {

    @Schema(description = "隐患ID")
    private Integer id;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "预测时间")
    private String predictTime;

}