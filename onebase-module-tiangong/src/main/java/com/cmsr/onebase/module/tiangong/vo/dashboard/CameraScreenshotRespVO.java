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
    
    @Schema(description = "摄像机信息")
    private String cameraInfo;
    
    @Schema(description = "地址")
    private String address;
    
    @Schema(description = "时间信息")
    private String timeInfo;
}