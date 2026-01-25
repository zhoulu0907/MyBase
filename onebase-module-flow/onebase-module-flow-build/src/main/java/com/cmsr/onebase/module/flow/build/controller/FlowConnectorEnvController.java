package com.cmsr.onebase.module.flow.build.controller;

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
    public PageResult<FlowConnectorEnvVO> pageEnvs(@Valid PageConnectorEnvReqVO pageReqVO) {
        return flowConnectorEnvService.pageEnvs(pageReqVO);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有环境配置（精简版）")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public PageResult<FlowConnectorEnvLiteVO> listAll(@Valid PageParam pageParam) {
        return flowConnectorEnvService.listAll(pageParam);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询环境配置详情")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public FlowConnectorEnvVO getEnvDetail(
            @Parameter(description = "主键ID") @PathVariable Long id) {
        return flowConnectorEnvService.getEnvDetail(id);
    }

    @GetMapping("/by-uuid/{envUuid}")
    @Operation(summary = "根据UUID查询环境配置详情")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public FlowConnectorEnvVO getEnvDetailByUuid(
            @Parameter(description = "环境配置UUID") @PathVariable String envUuid) {
        return flowConnectorEnvService.getEnvDetailByUuid(envUuid);
    }

    @GetMapping("/by-type/{typeCode}")
    @Operation(summary = "根据连接器类型查询环境配置列表")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:query')")
    public List<FlowConnectorEnvVO> listByTypeCode(
            @Parameter(description = "连接器类型编号") @PathVariable String typeCode) {
        return flowConnectorEnvService.listByTypeCode(typeCode);
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
    public List<EnvOptionVO> getEnvOptions(
            @Parameter(description = "连接器类型编号", example = "HTTP") @PathVariable String typeCode) {
        return flowConnectorEnvService.getEnvOptions(typeCode);
    }

    @PostMapping("/create")
    @Operation(summary = "创建环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:create')")
    public FlowConnectorEnvVO createEnv(@Valid @RequestBody CreateFlowConnectorEnvReqVO createVO) {
        return flowConnectorEnvService.createEnv(createVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:update')")
    public void updateEnv(@Valid @RequestBody UpdateFlowConnectorEnvReqVO updateVO) {
        flowConnectorEnvService.updateEnv(updateVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:delete')")
    public void deleteById(
            @Parameter(description = "主键ID") @PathVariable Long id) {
        flowConnectorEnvService.deleteById(id);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用环境配置")
    @PreAuthorize("@ss.hasPermission('flow:connector-env:update')")
    public void updateActiveStatus(
            @PathVariable Long id,
            @Parameter(description = "启用状态（0-禁用，1-启用）") @RequestParam Integer activeStatus) {
        flowConnectorEnvService.updateActiveStatus(id, activeStatus);
    }
}
