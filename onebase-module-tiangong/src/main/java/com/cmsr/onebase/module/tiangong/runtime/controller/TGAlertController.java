package com.cmsr.onebase.module.tiangong.runtime.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.tiangong.service.alert.TGAlertService;
import com.cmsr.onebase.module.tiangong.vo.alert.AlertResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "天工接口：提示框")
@Slf4j
@Validated
@RestController
@RequestMapping("/tiangong/alert")
public class TGAlertController {

    @Resource
    private TGAlertService tgAlertService;

    /**
     * 获取最新提示框内容
     */
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    @GetMapping("/get-latest-alerts")
    @Operation(summary ="获取最新提示框内容")
    public CommonResult<List<AlertResVO>> getLatestAlerts() {
        return success(tgAlertService.getLatestAlerts());
    }


}
