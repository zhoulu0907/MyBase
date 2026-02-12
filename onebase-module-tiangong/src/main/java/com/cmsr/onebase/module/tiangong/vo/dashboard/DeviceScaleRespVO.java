package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备规模统计响应 VO")
@Data
public class DeviceScaleRespVO {

    @Schema(description = "在线设备数量")
    private Integer onlineCount;

    @Schema(description = "离线设备数量")
    private Integer offlineCount;

    @Schema(description = "未激活设备数量")
    private Integer unactivatedCount;

    @Schema(description = "设备总数量")
    private Integer totalCount;

}