package com.cmsr.onebase.module.tiangong.runtime.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.tiangong.vo.dashboard.DeviceRuntimeParamResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "天工设备相关接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/tiangong/device")
public class TGDeviceController {

    /**
     * 获取设备运行参数（动态数据）
     */
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    @GetMapping("/device-runtime-params")
    @Operation(summary ="获取设备运行参数（动态数据）")
    public CommonResult<List<DeviceRuntimeParamResVO>> getDeviceRuntimeParams() {
        Random random = new Random();
        List<DeviceRuntimeParamResVO> paramList = List.of(
                createDeviceRuntimeParam("861556071807043", "鹿井士腾高速井泵", random),
                createDeviceRuntimeParam("861556071789019", "日虹背负永磁", random),
                createDeviceRuntimeParam("861556071807044", "天工智能泵站", random),
                createDeviceRuntimeParam("861556071789020", "智能工业电机", random)
        );
        return success(paramList);
    }


    // 私有辅助方法
    private DeviceRuntimeParamResVO createDeviceRuntimeParam(String deviceId, String deviceName, Random random) {
        DeviceRuntimeParamResVO param = new DeviceRuntimeParamResVO();
        param.setDeviceId(deviceId);
        param.setDeviceName(deviceName);
        param.setUpdateTime("2026-02-12 16:11:10");

        // 随机生成参数值
        param.setSpeed((double) (random.nextInt(100) + 20)); // 转速: 20-120
        param.setCurrent(random.nextDouble() * 10); // 电流: 0-10
        param.setOutputPower(random.nextDouble() * 5); // 输出功率: 0-5
        param.setRatedPower(random.nextDouble() * 10); // 额定功率: 0-10
        param.setOutputVoltage(random.nextDouble() * 400); // 输出电压: 0-400
        param.setInputVoltage(random.nextDouble() * 400); // 输入电压: 0-400
        param.setOutputCurrent(random.nextDouble() * 10); // 输出电流: 0-10

        // 故障码随机生成
        String[] faultCodes = {"正常", "母线过电压", "电机低速过流", "母线欠压", "温度过高", "通信异常"};
        param.setFaultCode(faultCodes[random.nextInt(faultCodes.length)]);

        // 运行时间: 10000-100000
        param.setRunTime((long) (random.nextDouble() * 90000) + 10000L);

        // 压力: 0-100
        param.setPressure(random.nextDouble() * 100);

        return param;
    }

}
