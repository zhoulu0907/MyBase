package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.build.vo.EnvOptionVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvVO;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorEnvService;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorEnvReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 连接器环境配置 Controller
 * <p>
 * 提供环境配置的REST API接口
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Slf4j
@Tag(name = "自动化工作流 - 连接器环境配置", description = "连接器环境配置管理接口")
@RestController
@RequestMapping("/flow/connector-env")
@Validated
public class FlowConnectorEnvController {

    @Resource
    private FlowConnectorEnvService flowConnectorEnvService;

    @GetMapping("/page")
    @Operation(summary = "分页查询环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public CommonResult<PageResult<FlowConnectorEnvVO>> pageEnvs(@Valid PageConnectorEnvReqVO pageReqVO) {
        PageResult<FlowConnectorEnvVO> result = flowConnectorEnvService.pageEnvs(pageReqVO);
        return CommonResult.success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有环境配置（精简版）")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public CommonResult<PageResult<FlowConnectorEnvLiteVO>> listAll(@Valid PageParam pageParam) {
        PageResult<FlowConnectorEnvLiteVO> result = flowConnectorEnvService.listAll(pageParam);
        return CommonResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询环境配置详情")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public CommonResult<FlowConnectorEnvVO> getEnvDetail(
            @Parameter(description = "主键ID") @PathVariable Long id) {
        FlowConnectorEnvVO result = flowConnectorEnvService.getEnvDetail(id);
        return CommonResult.success(result);
    }

    @GetMapping("/by-uuid/{envUuid}")
    @Operation(summary = "根据UUID查询环境配置详情")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public CommonResult<FlowConnectorEnvVO> getEnvDetailByUuid(
            @Parameter(description = "环境配置UUID") @PathVariable String envUuid) {
        FlowConnectorEnvVO result = flowConnectorEnvService.getEnvDetailByUuid(envUuid);
        return CommonResult.success(result);
    }

    @GetMapping("/by-type/{typeCode}")
    @Operation(summary = "根据连接器类型查询环境配置列表（精简版）",
              description = "返回环境配置的基本信息：id, envUuid, envName, typeCode")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public CommonResult<List<FlowConnectorEnvLiteVO>> listByTypeCode(
            @Parameter(description = "连接器类型编号") @PathVariable String typeCode,
            @Parameter(description = "租户ID（可选）") @RequestParam(required = false) Long tenantId) {
        List<FlowConnectorEnvLiteVO> result = flowConnectorEnvService.listByTypeCode(typeCode, tenantId);
        return CommonResult.success(result);
    }

    /**
     * 获取环境配置下拉选项
     * <p>
     * 用于连接器实例编辑页面选择环境配置
     * 返回格式化的 {value, label} 结构，适用于下拉选择框
     *
     * @param typeCode 连接器类型编号（如 HTTP, DATABASE_MYSQL）
     * @return 下拉选项列表
     */
    @GetMapping("/options/{typeCode}")
    @Operation(summary = "获取环境配置下拉选项",
              description = "用于连接器实例编辑页面的环境选择下拉框，返回启用的环境配置")
    public CommonResult<List<EnvOptionVO>> getEnvOptions(
            @Parameter(description = "连接器类型编号", example = "HTTP") @PathVariable String typeCode) {
        List<EnvOptionVO> result = flowConnectorEnvService.getEnvOptions(typeCode);
        return CommonResult.success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "创建环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:create')")
    public CommonResult<FlowConnectorEnvVO> createEnv(@Valid @RequestBody CreateFlowConnectorEnvReqVO createVO) {
        FlowConnectorEnvVO result = flowConnectorEnvService.createEnv(createVO);
        return CommonResult.success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:update')")
    public CommonResult<Boolean> updateEnv(@Valid @RequestBody UpdateFlowConnectorEnvReqVO updateVO) {
        flowConnectorEnvService.updateEnv(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:delete')")
    public CommonResult<Boolean> deleteById(
            @Parameter(description = "主键ID") @PathVariable Long id) {
        flowConnectorEnvService.deleteById(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:update')")
    public CommonResult<Boolean> updateActiveStatus(
            @PathVariable Long id,
            @Parameter(description = "启用状态（0-禁用，1-启用）") @RequestParam Integer activeStatus) {
        flowConnectorEnvService.updateActiveStatus(id, activeStatus);
        return CommonResult.success(Boolean.TRUE);
    }
}
