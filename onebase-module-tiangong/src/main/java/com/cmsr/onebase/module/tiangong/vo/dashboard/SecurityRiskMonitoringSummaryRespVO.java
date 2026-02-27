package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 安全风险监控概览统计信息 VO
 */
@Data
@Schema(description = "安全风险监控概览统计信息 VO")
public class SecurityRiskMonitoringSummaryRespVO {
    
    @Schema(description = "今日违规总数")
    private Integer todayViolationTotal;
    
    @Schema(description = "区域入侵检测数量")
    private Integer areaIntrusionDetection;
    
    @Schema(description = "非机动车违规告警数量")
    private Integer nonMotorVehicleViolation;
    
    @Schema(description = "漏油检测数量")
    private Integer oilLeakDetection;
    
    @Schema(description = "围栏识别数量")
    private Integer fenceRecognition;
    
    @Schema(description = "防尘胶检测数量")
    private Integer dustProofDetection;
    
    @Schema(description = "反光衣检测数量")
    private Integer reflectiveClothingDetection;
    
    @Schema(description = "翻牌检测数量")
    private Integer signFlipDetection;
    
    @Schema(description = "上装检测数量")
    private Integer upperClothingDetection;
    
    @Schema(description = "汽车违规检测数量")
    private Integer carViolationDetection;
    
    @Schema(description = "开门检测数量")
    private Integer doorOpeningDetection;
    
    @Schema(description = "搁置检测数量")
    private Integer placementDetection;
    
    @Schema(description = "人员靠近皮带检测数量")
    private Integer personnelNearBeltDetection;
    
    @Schema(description = "玩手机检测数量")
    private Integer phoneUsageDetection;
    
    @Schema(description = "摔倒检测数量")
    private Integer fallDetection;
}