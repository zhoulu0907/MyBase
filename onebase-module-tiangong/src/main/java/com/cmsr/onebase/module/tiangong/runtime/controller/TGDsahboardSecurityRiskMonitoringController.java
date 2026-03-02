package com.cmsr.onebase.module.tiangong.runtime.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.tiangong.vo.dashboard.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 安全风险监控大屏相关接口
 */
@Tag(name = "安全风险监控大屏相关接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/tiangong/security-risk-monitoring")
public class TGDsahboardSecurityRiskMonitoringController {

    /**
     * 获取今日违规统计信息（总数和各类检测的统计）
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/today-violation-summary")
    @Operation(summary = "获取今日违规统计信息")
    public CommonResult<SecurityRiskMonitoringSummaryRespVO> getTodayViolationSummary() {
        SecurityRiskMonitoringSummaryRespVO respVO = new SecurityRiskMonitoringSummaryRespVO();
        respVO.setTodayViolationTotal(310);
        respVO.setAreaIntrusionDetection(270);
        respVO.setNonMotorVehicleViolation(0);
        respVO.setOilLeakDetection(0);
        respVO.setFenceRecognition(0);
        respVO.setDustProofDetection(0);
        respVO.setReflectiveClothingDetection(0);
        respVO.setSignFlipDetection(0);
        respVO.setUpperClothingDetection(0);
        respVO.setCarViolationDetection(0);
        respVO.setDoorOpeningDetection(0);
        respVO.setSmokeDetection(0);
        respVO.setPlacementDetection(0);
        respVO.setPersonnelNearBeltDetection(0);
        respVO.setPhoneUsageDetection(0);
        respVO.setFallDetection(0);
        return success(respVO);
    }

    /**
     * 获取今日地址违规TOP5
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/violation-address-top5")
    @Operation(summary = "获取今日地址违规TOP5")
    public CommonResult<List<ViolationAddressTop5RespVO>> getViolationAddressTop5() {
        List<ViolationAddressTop5RespVO> top5List = List.of(
                createViolationAddressTop5(1, "三明", 1344),
                createViolationAddressTop5(2, "郑州", 9822),
                createViolationAddressTop5(3, "周口", 8912),
                createViolationAddressTop5(4, "漯河", 5834),
                createViolationAddressTop5(5, "信阳", 5875)
        );
        return success(top5List);
    }

    /**
     * 获取违规数据趋势
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/violation-trend")
    @Operation(summary = "获取违规数据趋势")
    public CommonResult<List<ViolationTrendItemResVO>> getViolationTrend() {
        List<ViolationTrendItemResVO> trendList = List.of(
                createViolationTrendItem("1月", 680),
                createViolationTrendItem("2月", 1480),
                createViolationTrendItem("3月", 2580),
                createViolationTrendItem("4月", 3880),
                createViolationTrendItem("5月", 5380),
                createViolationTrendItem("6月", 7000),
                createViolationTrendItem("7月", 8900)
        );
        return success(trendList);
    }

    /**
     * 获取违规分布
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/violation-distribution")
    @Operation(summary = "获取违规分布")
    public CommonResult<List<ViolationDistributionRespVO>> getViolationDistribution() {
        List<ViolationDistributionRespVO> distributionList = List.of(
                createViolationDistribution("厦门", 20),
                createViolationDistribution("沈阳", 40),
                createViolationDistribution("北京", 60),
                createViolationDistribution("上海", 80),
                createViolationDistribution("新疆", 100)
        );
        return success(distributionList);
    }

    /**
     * 获取当前摄像机截屏列表
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/camera-screenshot-list")
    @Operation(summary = "获取当前摄像机截屏列表")
    public CommonResult<List<CameraScreenshotRespVO>> getCameraScreenshotList() {
        List<CameraScreenshotRespVO> screenshotList = List.of(
                createCameraScreenshot(1, "人员12", "A区 一楼", "2分钟前"),
                createCameraScreenshot(2, "人员12", "A区 一楼", "3分钟前"),
                createCameraScreenshot(3, "人员12", "A区 一楼", "4分钟前"),
                createCameraScreenshot(4, "人员12", "A区 一楼", "5分钟前"),
                createCameraScreenshot(5, "人员12", "A区 一楼", "6分钟前"),
                createCameraScreenshot(6, "人员12", "A区 一楼", "7分钟前"),
                createCameraScreenshot(7, "人员12", "A区 一楼", "8分钟前"),
                createCameraScreenshot(8, "人员12", "A区 一楼", "9分钟前"),
                createCameraScreenshot(9, "人员12", "A区 一楼", "10分钟前"),
                createCameraScreenshot(10, "人员12", "A区 一楼", "11分钟前")
        );
        return success(screenshotList);
    }

    // 私有辅助方法
    private ViolationAddressTop5RespVO createViolationAddressTop5(Integer rank, String addressName, Integer violationCount) {
        ViolationAddressTop5RespVO vo = new ViolationAddressTop5RespVO();
        vo.setRank(rank);
        vo.setAddressName(addressName);
        vo.setViolationCount(violationCount);
        return vo;
    }

    private ViolationTrendItemResVO createViolationTrendItem(String timePoint, Integer violationCount) {
        ViolationTrendItemResVO item = new ViolationTrendItemResVO();
        item.setTimePoint(timePoint);
        item.setViolationCount(violationCount);
        return item;
    }

    private ViolationDistributionRespVO createViolationDistribution(String regionName, Integer violationCount) {
        ViolationDistributionRespVO vo = new ViolationDistributionRespVO();
        vo.setRegionName(regionName);
        vo.setViolationCount(violationCount);
        return vo;
    }

    private CameraScreenshotRespVO createCameraScreenshot(Integer serialNumber, String cameraInfo, String address, String timeInfo) {
        CameraScreenshotRespVO vo = new CameraScreenshotRespVO();
        vo.setSerialNumber(serialNumber);
        vo.setCameraInfo(cameraInfo);
        vo.setAddress(address);
        vo.setTimeInfo(timeInfo);
        return vo;
    }
}