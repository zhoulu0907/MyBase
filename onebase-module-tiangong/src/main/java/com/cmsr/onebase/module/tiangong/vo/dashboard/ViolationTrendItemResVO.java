package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 违规数据趋势项 VO
 */
@Data
@Schema(description = "违规数据趋势项 VO")
public class ViolationTrendItemResVO {
    
    @Schema(description = "时间点（如：1月、2月等）")
    private String timePoint;
    
    @Schema(description = "违规数量")
    private Integer violationCount;
}