package com.cmsr.onebase.module.system.build.controller.config;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.service.config.SystemConfigService;
import com.cmsr.onebase.module.system.vo.config.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 参数配置")
@RestController
@RequestMapping("/system/config")
@Validated
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建参数配置")
    @PreAuthorize("@ss.hasPermission('tenant:config:create')")
    public CommonResult<Long> createConfig(@Valid @RequestBody SystemGeneralConfigSaveReqVO createReqVO) {
        return success(systemConfigService.createConfig(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改参数配置")
    @PreAuthorize("@ss.hasPermission('tenant:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody SystemGeneralConfigUpdateReqVO updateReqVO) {
        systemConfigService.updateConfig(updateReqVO);
        return success(true);
    }


    @PostMapping("/update-status")
    @Operation(summary = "修改状态-禁用/启用")
    @PreAuthorize("@ss.hasPermission('tenant:config:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        systemConfigService.updateStatus(id,status);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除配置项")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:config:delete')")
    public CommonResult<Boolean> deleteConfig(@RequestParam("id") Long id) {
        systemConfigService.deleteConfig(id);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "获得参数配置")
    @PreAuthorize("@ss.hasPermission('tenant:config:query')")
    public CommonResult<SystemGeneralConfigRespVO> getConfig(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(systemConfigService.getConfig(id), SystemGeneralConfigRespVO.class));
    }



    @GetMapping("/list")
    @Operation(summary = "获得配置项列表-不分页")
    @PreAuthorize("@ss.hasPermission('tenant:config:query')")
    public CommonResult<List<SystemGeneralConfigRespVO>> getTenantConfigList(@Valid SystemConfigReqVO configReqVO) {
         List<SystemGeneralConfigDO> pageResult = systemConfigService.getTenantConfigList(configReqVO);
        return success(BeanUtils.toBean(pageResult, SystemGeneralConfigRespVO.class));
    }


    @GetMapping("/list-by-keys")
    @Operation(summary = "获得配置项列表-不分页")
    @PreAuthorize("@ss.hasPermission('tenant:config:query')")
    public CommonResult<List<SystemGeneralConfigRespVO>> getTenantConfigList(@Valid SystemConfigSearchReqVO configReqVO) {
        List<SystemGeneralConfigDO> pageResult = systemConfigService.getTenantConfigListByKeysAndAppId(configReqVO);
        return success(BeanUtils.toBean(pageResult, SystemGeneralConfigRespVO.class));
    }

}
