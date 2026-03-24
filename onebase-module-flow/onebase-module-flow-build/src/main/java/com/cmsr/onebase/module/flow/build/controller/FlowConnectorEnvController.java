package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorEnvService;
import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 连接器环境配置控制器
 * <p>
 * 提供环境配置的管理接口，所有连接器类型的环境配置都通过此接口管理
 *
 * @author onebase
 * @since 2026-03-20
 */
@Tag(name = "连接器环境配置", description = "连接器环境配置接口")
@RestController
@RequestMapping("/flow/connector-env")
@Validated
public class FlowConnectorEnvController {

    @Resource
    private FlowConnectorEnvService envService;

    @Operation(summary = "查询环境配置列表", description = "获取指定连接器的环境配置列表")
    @GetMapping("/list")
    public CommonResult<List<FlowConnectorEnvLiteVO>> getEnvironments(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId) {
        List<FlowConnectorEnvLiteVO> environments = envService.getEnvironments(connectorId);
        return CommonResult.success(environments);
    }

    @Operation(summary = "查询指定环境配置", description = "获取连接器指定环境的配置信息")
    @GetMapping("/detail")
    public CommonResult<EnvironmentConfigVO> getEnvironmentConfig(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId,
            @Parameter(description = "环境编码", required = true, example = "DEV")
            @RequestParam("envCode") @NotBlank(message = "环境编码不能为空") String envCode) {
        EnvironmentConfigVO config = envService.getEnvironmentConfig(connectorId, envCode);
        return CommonResult.success(config);
    }

    @Operation(summary = "获取环境配置模板", description = "获取连接器类型对应的环境配置 Formily Schema 模板")
    @GetMapping("/template")
    public CommonResult<EnvConfigTemplateVO> getEnvConfigTemplate(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId) {
        EnvConfigTemplateVO template = envService.getEnvConfigTemplate(connectorId);
        return CommonResult.success(template);
    }

    @Operation(summary = "创建环境配置", description = "保存新的环境配置，如果环境已存在则拒绝")
    @PostMapping("/create")
    public CommonResult<Boolean> createEnvironmentConfig(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId,
            @RequestBody @Valid SaveEnvironmentConfigReqVO reqVO) {
        Boolean result = envService.saveEnvironmentConfig(connectorId, reqVO);
        return CommonResult.success(result);
    }

    @Operation(summary = "更新环境配置", description = "更新已存在的环境配置，环境必须存在")
    @PostMapping("/update")
    public CommonResult<Boolean> updateEnvironmentConfig(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId,
            @RequestBody @Valid SaveEnvironmentConfigReqVO reqVO) {
        Boolean result = envService.updateEnvironmentConfig(connectorId, reqVO);
        return CommonResult.success(result);
    }

    @Operation(summary = "设置启用环境", description = "设置连接器当前启用的环境，传空取消启用")
    @PostMapping("/enable")
    public CommonResult<Boolean> enableEnvironment(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId,
            @Parameter(description = "环境编码（传空表示取消启用）", required = false)
            @RequestParam(value = "envCode", required = false) String envCode) {
        Boolean result = envService.enableEnvironment(connectorId, envCode);
        return CommonResult.success(result);
    }

    @Operation(summary = "获取启用环境", description = "获取连接器当前启用的环境完整信息")
    @GetMapping("/enabled-env")
    public CommonResult<FlowConnectorEnvLiteVO> getEnabledEnv(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @RequestParam("connectorId") Long connectorId) {
        FlowConnectorEnvLiteVO enabledEnv = envService.getEnabledEnv(connectorId);
        return CommonResult.success(enabledEnv);
    }
}