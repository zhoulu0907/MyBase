package com.cmsr.onebase.module.tiangong.runtime.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.tiangong.vo.dashboard.*;
import com.cmsr.onebase.module.tiangong.vo.dashboard.DashboardSummaryRespVO;
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

@Tag(name = "天工仪表盘相关接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/tiangong/dashboard")
public class TGDashboardController {



    @PermitAll
    @ApiSignIgnore
    @GetMapping("/device-info-all")
    @Operation(summary = "获取设备信息")
    public CommonResult<DeviceInfoRespVO> getDeviceInfo(@RequestBody @Valid DashboardInfoReqVO reqVO) {
        DeviceInfoRespVO respVO = new DeviceInfoRespVO();
        respVO.setTest("haha");
        return success(respVO);
    }

    /**
     *
     * 获取设备规模统计
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/device-scale")
    @Operation(summary ="获取设备规模统计")
    public CommonResult<DeviceScaleRespVO> getDeviceScale() {
        DeviceScaleRespVO respVO = new DeviceScaleRespVO();
        respVO.setOnlineCount(30000);
        respVO.setOfflineCount(2000);
        respVO.setUnactivatedCount(2000);
        respVO.setTotalCount(34000);
        return success(respVO);
    }

    /**
     *
     * 获取累计设备接入数趋势
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/cumulative-device-count-trend")
    @Operation(summary ="获取累计设备接入数趋势")
    public CommonResult<List<DeviceTrendItemResVO>> getCumulativeDeviceCountTrend() {
        List<DeviceTrendItemResVO> trendList = List.of(
                createTrendItem("1月", 680),
                createTrendItem("2月", 1480),
                createTrendItem("3月", 2580),
                createTrendItem("4月", 3880),
                createTrendItem("5月", 5380),
                createTrendItem("6月", 7000),
                createTrendItem("7月", 8900)
        );
        return success(trendList);
    }

    /**
     * 获取设备隐患列表
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/device-hazards")
    @Operation(summary ="获取设备隐患列表")
    public CommonResult<List<DeviceHazardResVO>> getDeviceHazards() {
        List<DeviceHazardResVO> hazardList = List.of(
                createDeviceHazard(1, "浙江省", "直连泵1","主轴断裂", "2026-02-10"),
                createDeviceHazard(2, "江苏省", "直连泵2","主轴断裂", "2026-02-10"),
                createDeviceHazard(3, "山东省", "直连泵3","主轴断裂", "2026-02-10")
        );
        return success(hazardList);
    }

    /**
     * 获取预测性隐患数量趋势
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/predictive-hazard-trend")
    @Operation(summary ="获取预测性隐患数量趋势")
    public CommonResult<List<DeviceTrendItemResVO>> getPredictiveHazardTrend() {
        List<DeviceTrendItemResVO> trendList = List.of(
                createTrendItem("1月", 100),
                createTrendItem("2月", 100),
                createTrendItem("3月", 150)
        );
        return success(trendList);
    }

    /**
     * 获取待处理隐患工单列表
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/pending-hazard-work-orders")
    @Operation(summary ="获取待处理隐患工单列表")
    public CommonResult<List<PendingHazardWorkOrderResVO>> getPendingHazardWorkOrders() {
        List<PendingHazardWorkOrderResVO> workOrderList = List.of(
                createPendingWorkOrder(1, "G1219318", "主轴断裂", "2026-02-10"),
                createPendingWorkOrder(2, "G1219319", "主轴断裂", "2026-02-10")
        );
        return success(workOrderList);
    }

    /**
     * 获取隐患工单列表（用于大屏展示）
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/hazard-work-order-list")
    @Operation(summary ="隐患工单列表")
    public CommonResult<List<HazardWorkOrderListResVO>> getHazardWorkOrderList() {
        List<HazardWorkOrderListResVO> workOrderList = List.of(
                createHazardWorkOrder(1, "G1219318", "861556071807043", "高速井泵", "张三", "+86 13800138000", "2026-03-05", "处理中"),
                createHazardWorkOrder(2, "G1219319", "861556071789019", "永磁电机", "李四", "+86 13900139000", "2026-03-06", "待处理"),
                createHazardWorkOrder(3, "G1219320", "861556071789020", "变频器", "王五", "+86 13700137000", "2026-03-07", "已完成")
        );
        return success(workOrderList);
    }

    /**
     * 获取告警列表
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/alarm-list")
    @Operation(summary ="获取告警列表")
    public CommonResult<List<DeviceAlarmResVO>> getAlarmList() {
        List<DeviceAlarmResVO> alarmList = List.of(
                createDeviceAlarm(1, "861556071807043", "鹿井士腾高速井泵", "电机低速过流", "2025-11-18"),
                createDeviceAlarm(2, "861556071789019", "日虹背负永磁", "母线欠压", "2025-11-15")
        );
        return success(alarmList);
    }

    /**
     * 获取设备故障检测统计
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/device-fault-detection")
    @Operation(summary ="获取设备故障检测统计")
    public CommonResult<DeviceFaultDetectionRespVO> getDeviceFaultDetection() {
        DeviceFaultDetectionRespVO respVO = new DeviceFaultDetectionRespVO();
        respVO.setRealTimeAlarms(120);
        respVO.setHistoricalAlarms(1063);
        return success(respVO);
    }

    /**
     * 获取仪表盘概览统计信息（设备接入数、区域覆盖、注册用户数）
     */
    @PermitAll
    @ApiSignIgnore
    @GetMapping("/dashboard-summary")
    @Operation(summary = "获取仪表盘概览统计信息")
    public CommonResult<DashboardSummaryRespVO> getDashboardSummary() {
        DashboardSummaryRespVO respVO = new DashboardSummaryRespVO();
        respVO.setDeviceCount(72000L);
        respVO.setRegionCoverage("24省120市");
        respVO.setUserCount(27000L);
        return success(respVO);
    }

    // 私有辅助方法
    private DeviceTrendItemResVO createTrendItem(String month, Integer count) {
        DeviceTrendItemResVO item = new DeviceTrendItemResVO();
        item.setMonth(month);
        item.setCount(count);
        return item;
    }
    //
    private DeviceHazardResVO createDeviceHazard(Integer id, String province, String deviceName, String hazardName, String predictTime) {
        DeviceHazardResVO hazard = new DeviceHazardResVO();
        hazard.setId(id);
        hazard.setProvince(province);
        hazard.setDeviceName(deviceName);
        hazard.setHazardName(hazardName);
        hazard.setPredictTime(predictTime);
        return hazard;
    }

    private PendingHazardWorkOrderResVO createPendingWorkOrder(Integer id, String workOrderNo, String hazardName, String createTime) {
        PendingHazardWorkOrderResVO workOrder = new PendingHazardWorkOrderResVO();
        workOrder.setId(id);
        workOrder.setWorkOrderNo(workOrderNo);
        workOrder.setHazardName(hazardName);
        workOrder.setCreateTime(createTime);
        return workOrder;
    }

    private HazardWorkOrderListResVO createHazardWorkOrder(Integer seqNo, String workOrderNo, String deviceId, String deviceModel, String contactPerson, String contactPhone, String appointmentTime, String workOrderStatus) {
        HazardWorkOrderListResVO workOrder = new HazardWorkOrderListResVO();
        workOrder.setSeqNo(seqNo);
        workOrder.setWorkOrderNo(workOrderNo);
        workOrder.setDeviceId(deviceId);
        workOrder.setDeviceModel(deviceModel);
        workOrder.setContactPerson(contactPerson);
        workOrder.setContactPhone(contactPhone);
        workOrder.setAppointmentTime(appointmentTime);
        workOrder.setWorkOrderStatus(workOrderStatus);
        return workOrder;
    }

    private DeviceAlarmResVO createDeviceAlarm(Integer id, String deviceId, String deviceName, String alarmName, String alarmTime) {
        DeviceAlarmResVO alarm = new DeviceAlarmResVO();
        alarm.setId(id);
        alarm.setDeviceId(deviceId);
        alarm.setDeviceName(deviceName);
        alarm.setAlarmName(alarmName);
        alarm.setAlarmTime(alarmTime);
        return alarm;
    }

}
