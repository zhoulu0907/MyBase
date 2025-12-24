package com.cmsr.onebase.module.app.runtime.controller.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.runtime.service.app.AppApplicationService;
import com.cmsr.onebase.module.app.runtime.vo.app.AppLeastInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/7/22 14:48
 */
@Setter
@Tag(name = "应用管理")
@RestController
@RequestMapping("/app/application")
@Validated
public class AppApplicationController {

    @Resource
    private AppApplicationService appApplicationService;

    /**
     * // TODO 前端切换后，这个  @PermitAll 要删除
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @Operation(summary = "获得应用")
    @TenantIgnore
    @PermitAll
    public CommonResult<ApplicationRespVO> getApplication(@RequestParam("id") Long id) {
        return CommonResult.success(appApplicationService.getApplication(id));
    }


    @GetMapping("/least")
    @Operation(summary = "获得应用")
    @TenantIgnore
    @PermitAll
    public CommonResult<AppLeastInfo> getApplicationLeastInfo(@RequestParam("id") Long id) {
        return CommonResult.success(appApplicationService.getApplicationLeastInfo(id));
    }


    @GetMapping("/get-navigation-config")
    @Operation(summary = "获取应用导航配置")
    public CommonResult<ApplicationNavigationConfigVO> getApplicationNavigationConfig(@RequestParam("id") Long id) {
        return CommonResult.success(appApplicationService.getApplicationNavigationConfig(id));
    }

}
