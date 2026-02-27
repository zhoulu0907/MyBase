package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 摄像机截屏信息 VO
 */
@Data
@Schema(description = "摄像机截屏信息 VO")
public class CameraScreenshotRespVO {
    
    @Schema(description = "序号")
    private Integer serialNumber;
    
    @Schema(description = "列1内容")
    private String column1;
    
    @Schema(description = "列2内容")
    private String column2;
    
    @Schema(description = "列3内容")
    private String column3;
}