package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 违规分布 VO
 */
@Data
@Schema(description = "违规分布 VO")
public class ViolationDistributionRespVO {
    
    @Schema(description = "地区名称")
    private String regionName;
    
    @Schema(description = "违规数量")
    private Integer violationCount;
}