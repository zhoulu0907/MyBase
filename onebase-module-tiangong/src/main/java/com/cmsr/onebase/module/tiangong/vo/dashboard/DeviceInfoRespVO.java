package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备信息响应 VO")
@Data
public class DeviceInfoRespVO {

    @Schema(description = "测试数据")
    private String test;

}