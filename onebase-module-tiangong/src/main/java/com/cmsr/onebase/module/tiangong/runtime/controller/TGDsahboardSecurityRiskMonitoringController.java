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
                createCameraScreenshot(1, "行1列1", "行1列2", "行1列3"),
                createCameraScreenshot(2, "行2列1", "行2列2", "行2列3"),
                createCameraScreenshot(3, "行3列1", "行3列2", "行3列3"),
                createCameraScreenshot(4, "行4列1", "行4列2", "行4列3"),
                createCameraScreenshot(5, "行5列1", "行5列2", "行5列3"),
                createCameraScreenshot(6, "行6列1", "行6列2", "行6列3"),
                createCameraScreenshot(7, "行7列1", "行7列2", "行7列3"),
                createCameraScreenshot(8, "行8列1", "行8列2", "行8列3"),
                createCameraScreenshot(9, "行9列1", "行9列2", "行9列3"),
                createCameraScreenshot(10, "行10列1", "行10列2", "行10列3")
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

    private CameraScreenshotRespVO createCameraScreenshot(Integer serialNumber, String column1, String column2, String column3) {
        CameraScreenshotRespVO vo = new CameraScreenshotRespVO();
        vo.setSerialNumber(serialNumber);
        vo.setColumn1(column1);
        vo.setColumn2(column2);
        vo.setColumn3(column3);
        return vo;
    }
}