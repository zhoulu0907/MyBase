package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorService;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "连接器", description = "连接器接口")
@RestController
@RequestMapping("/flow/connector")
@Validated
public class FlowConnectorController {

    @Resource
    private FlowConnectorService connectorService;

    @Operation(summary = "分页查询连接器实例", description = "返回精简VO，包含实例名称、类型、环境信息、配置状态、启用状态等列表展示所需字段")
    @GetMapping("/page")
    public CommonResult<PageResult<FlowConnectorLiteVO>> pageQuery(@Valid PageConnectorReqVO pageConnectorReqVO) {
        PageResult<FlowConnectorLiteVO> connectorPage = connectorService.pageConnectors(pageConnectorReqVO);
        return CommonResult.success(connectorPage);
    }

    @Operation(summary = "获取连接器详情")
    @GetMapping("/{id}")
    public CommonResult<FlowConnectorVO> getConnector(@PathVariable Long id) {
        FlowConnectorVO connectorDetail = connectorService.getConnectorDetail(id);
        return CommonResult.success(connectorDetail);
    }

    @Operation(summary = "创建连接器")
    @PostMapping("/create")
    public CommonResult<CreateFlowConnectorRespVO> createConnectorBrief(@RequestBody @Valid CreateFlowConnectorReqVO createVO) {
        CreateFlowConnectorRespVO result = connectorService.createConnector(createVO);
        return CommonResult.success(result);
    }

    @Operation(summary = "更新连接器")
    @PutMapping("/{id}")
    public CommonResult<Boolean> updateConnector(@PathVariable Long id, @RequestBody @Valid UpdateFlowConnectorReqVO updateVO) {
        // 确保updateVO中的id与路径参数一致
        updateVO.setId(id);
        connectorService.updateConnector(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "更新连接器基本信息",
              description = "只更新描述信息，自动检测变化")
    @Parameter(name = "id", description = "连接器ID", required = true)
    @PutMapping("/{id}/base-info")
    public CommonResult<Boolean> updateBaseInfo(
            @PathVariable Long id,
            @RequestBody @Valid UpdateFlowConnectorReqVO updateVO) {
        Boolean updated = connectorService.updateBaseInfo(id, updateVO);
        return CommonResult.success(updated);
    }

    @Operation(summary = "删除连接器")
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteConnector(@PathVariable Long id) {
        connectorService.deleteById(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "查询所有连接器实例")
    @GetMapping("/list-all")
    public CommonResult<PageResult<FlowConnectorLiteVO>> listAll(PageParam pageParam) {
        PageResult<FlowConnectorLiteVO> result = connectorService.listAll(pageParam);
        return CommonResult.success(result);
    }

    @Operation(summary = "启用/禁用连接器实例",
              description = "启用或禁用连接器实例")
    @Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
    @Parameter(name = "activeStatus", description = "启用状态（0-禁用，1-启用）", required = true, example = "1")
    @PutMapping("/{id}/status")
    public CommonResult<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam("activeStatus") Integer activeStatus) {
        connectorService.updateActiveStatus(id, activeStatus);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "根据连接器类型查询实例列表",
              description = "返回指定类型的所有连接器实例，不包含分页")
    @Parameter(name = "typeCode", description = "连接器类型编码（如 DATABASE_MYSQL、HTTP）", required = true, example = "DATABASE_MYSQL")
    @GetMapping("/by-type/{typeCode}")
    public CommonResult<List<FlowConnectorVO>> listByType(@PathVariable String typeCode) {
        List<FlowConnectorVO> result = connectorService.listByType(typeCode);
        return CommonResult.success(result);
    }

    // ==================== 环境配置接口 ====================

    @Operation(summary = "查询连接器的环境配置列表",
              description = "从flow_connector.config字段解析环境配置信息")
    @Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
    @GetMapping("/{id}/environments")
    public CommonResult<List<FlowConnectorEnvLiteVO>> getEnvironments(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long id) {
        List<FlowConnectorEnvLiteVO> environments = connectorService.getEnvironments(id);
        return CommonResult.success(environments);
    }

    @Operation(summary = "查询连接器的指定环境配置信息",
              description = "从flow_connector.config的properties中解析出指定环境的Formily Schema")
    @Parameter(name = "id", description = "连接器实例ID（主键）", required = true, example = "1")
    @Parameter(name = "envCode", description = "环境编码（如DEV、TEST、PROD）", required = true, example = "PROD")
    @GetMapping("/{id}/environment-config")
    public CommonResult<EnvironmentConfigVO> getEnvironmentConfig(
            @PathVariable("id") Long id,
            @RequestParam("envCode") @NotBlank(message = "环境编码不能为空") String envCode) {

        EnvironmentConfigVO config = connectorService.getEnvironmentConfig(id, envCode);
        return CommonResult.success(config);
    }

    @Operation(summary = "获取环境配置模板",
              description = "获取连接器类型对应的环境配置 Formily Schema 模板，用于创建环境信息")
    @Parameter(name = "id", description = "连接器实例ID", required = true)
    @GetMapping("/{id}/env-config-template")
    public CommonResult<EnvConfigTemplateVO> getEnvConfigTemplate(@PathVariable Long id) {
        EnvConfigTemplateVO template = connectorService.getEnvConfigTemplate(id);
        return CommonResult.success(template);
    }

    @Operation(summary = "保存连接器环境配置",
              description = "保存新的环境配置到 connector.config，如果环境已存在则拒绝")
    @Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
    @PostMapping("/{id}/save-env")
    public CommonResult<Boolean> saveEnvironmentConfig(
            @PathVariable("id") Long id,
            @RequestBody @Valid SaveEnvironmentConfigReqVO reqVO) {
        Boolean result = connectorService.saveEnvironmentConfig(id, reqVO);
        return CommonResult.success(result);
    }

    // ==================== 动作管理接口 ====================

    @Operation(summary = "获取动作配置模板",
              description = "获取连接器类型对应的动作配置 Formily Schema 模板，用于创建动作信息")
    @Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
    @GetMapping("/{id}/action-config-template")
    public CommonResult<ActionConfigTemplateVO> getActionConfigTemplate(@PathVariable Long id) {
        ActionConfigTemplateVO template = connectorService.getActionConfigTemplate(id);
        return CommonResult.success(template);
    }

    @Operation(summary = "保存连接器动作配置",
              description = "保存新的动作配置到 connector.action_config，如果动作已存在则拒绝")
    @Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
    @PostMapping("/{id}/save-action")
    public CommonResult<Boolean> saveActionConfig(
            @PathVariable("id") Long id,
            @RequestBody @Valid SaveActionConfigReqVO reqVO) {
        Boolean result = connectorService.saveActionConfig(id, reqVO);
        return CommonResult.success(result);
    }

    @Operation(summary = "查询连接器动作清单")
    @GetMapping("/{id}/actions")
    public CommonResult<List<String>> getActions(@PathVariable Long id) {
        List<String> actions = connectorService.getActionsById(id);
        return CommonResult.success(actions);
    }

    @Operation(summary = "获取连接器的动作列表", description = "返回连接器的动作配置列表（精简版）")
    @GetMapping("/{id}/action-infos")
    public CommonResult<List<ConnectorActionLiteVO>> getActionInfos(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long id) {
        List<ConnectorActionLiteVO> actions = connectorService.getActionInfos(id);
        return CommonResult.success(actions);
    }

    @Operation(summary = "获取动作详情")
    @GetMapping("/{connectorId}/actions/{actionName}")
    public CommonResult<ConnectorActionVO> getActionDetail(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        ConnectorActionVO action = connectorService.getActionDetail(connectorId, actionName);
        return CommonResult.success(action);
    }

    @Operation(summary = "保存动作草稿")
    @PostMapping("/{connectorId}/actions")
    public CommonResult<String> saveActionDraft(
            @PathVariable Long connectorId,
            @RequestBody @Valid CreateConnectorActionReqVO createVO) {
        String actionName = connectorService.saveActionDraft(connectorId, createVO);
        return CommonResult.success(actionName);
    }

    @Operation(summary = "更新动作草稿")
    @PutMapping("/{connectorId}/actions/{actionName}")
    public CommonResult<Boolean> updateActionDraft(
            @PathVariable Long connectorId,
            @PathVariable String actionName,
            @RequestBody @Valid UpdateConnectorActionReqVO updateVO) {
        connectorService.updateActionDraft(connectorId, actionName, updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "发布动作")
    @PutMapping("/{connectorId}/actions/{actionName}/publish")
    public CommonResult<Boolean> publishAction(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        connectorService.publishAction(connectorId, actionName);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "下架动作")
    @PutMapping("/{connectorId}/actions/{actionName}/offline")
    public CommonResult<Boolean> offlineAction(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        connectorService.offlineAction(connectorId, actionName);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "重新上线动作")
    @PutMapping("/{connectorId}/actions/{actionName}/republish")
    public CommonResult<Boolean> republishAction(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        connectorService.republishAction(connectorId, actionName);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "复制动作")
    @PostMapping("/{connectorId}/actions/{actionName}/copy")
    public CommonResult<String> copyAction(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        String newActionName = connectorService.copyAction(connectorId, actionName);
        return CommonResult.success(newActionName);
    }

    @Operation(summary = "删除动作")
    @DeleteMapping("/{connectorId}/actions/{actionName}/delete")
    public CommonResult<Boolean> deleteAction(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        connectorService.deleteAction(connectorId, actionName);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "校验动作是否可发布")
    @PostMapping("/{connectorId}/actions/{actionName}/validate")
    public CommonResult<Boolean> validateActionForPublish(
            @PathVariable Long connectorId,
            @PathVariable String actionName) {
        ActionConfigHelper.ValidationResult result =
                connectorService.validateActionForPublish(connectorId, actionName);
        return CommonResult.success(result.isValid());
    }
}
