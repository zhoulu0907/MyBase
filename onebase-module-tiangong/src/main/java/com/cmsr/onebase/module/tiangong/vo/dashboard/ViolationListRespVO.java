package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 违规列表响应VO
 */
@Data
public class ViolationListRespVO {

    @Schema(description = "序号")
    private Integer sequenceNumber;

    @Schema(description = "违规类型")
    private String violationType;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "摄像机")
    private String camera;

    @Schema(description = "时间")
    private LocalDateTime time;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "图片URL")
    private String imageUrl;
}