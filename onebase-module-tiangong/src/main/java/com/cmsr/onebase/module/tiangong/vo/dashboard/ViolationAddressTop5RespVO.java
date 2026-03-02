package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 违规地址TOP5 VO
 */
@Data
@Schema(description = "违规地址TOP5 VO")
public class ViolationAddressTop5RespVO {
    
    @Schema(description = "排名")
    private Integer rank;
    
    @Schema(description = "地址名称")
    private String addressName;
    
    @Schema(description = "违规次数")
    private Integer violationCount;
}