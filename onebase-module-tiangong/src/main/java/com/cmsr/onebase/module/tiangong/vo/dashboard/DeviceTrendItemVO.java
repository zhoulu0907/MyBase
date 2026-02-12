package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备接入趋势数据项 VO")
@Data
public class DeviceTrendItemVO {

    @Schema(description = "月份")
    private String month;

    @Schema(description = "设备数量")
    private Integer count;

}