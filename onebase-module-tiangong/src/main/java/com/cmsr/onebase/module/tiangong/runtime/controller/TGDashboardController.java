package com.cmsr.onebase.module.tiangong.runtime.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.tiangong.utils.JsonFileReaderUtil;
import com.cmsr.onebase.module.tiangong.vo.dashboard.DashboardInfoReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "天工仪表盘相关接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/tiangong/dashboard")
public class TGDashboardController {

    private final JsonFileReaderUtil jsonFileReaderUtil;

    public TGDashboardController(JsonFileReaderUtil jsonFileReaderUtil) {
        this.jsonFileReaderUtil = jsonFileReaderUtil;
    }

    @PermitAll
    @ApiSignIgnore
    @GetMapping("/device-info-all")
    @Operation(summary = "获取设备信息")
    @TenantIgnore
    public CommonResult<Map<String, Object>> getDeviceInfo(@Valid DashboardInfoReqVO reqVO) {
        // 直接读取并返回JSON文件内容
        String jsonFilePath = "json/柱状图测试数据.json";
        Map<String, Object> jsonData = jsonFileReaderUtil.readJsonFileToMap(jsonFilePath);
        return success(jsonData);
    }
}