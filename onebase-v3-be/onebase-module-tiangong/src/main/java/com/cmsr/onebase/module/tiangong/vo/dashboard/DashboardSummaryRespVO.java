package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 仪表盘概览统计信息 Response VO
 */
@Data
@Schema(description = "仪表盘概览统计信息 Response VO")
public class DashboardSummaryRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备接入数", example = "72000")
    private Long deviceCount;

    @Schema(description = "区域覆盖", example = "24省120市")
    private String regionCoverage;

    @Schema(description = "注册用户数", example = "27000")
    private Long userCount;

}