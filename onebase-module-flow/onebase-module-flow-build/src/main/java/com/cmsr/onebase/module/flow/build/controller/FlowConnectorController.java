package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorService;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.databind.JsonNode;
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

    // ==================== 动作管理接口 ====================

    @Operation(summary = "查询连接器动作清单")
    @GetMapping("/{id}/actions")
    public CommonResult<List<String>> getActions(@PathVariable Long id) {
        List<String> actions = connectorService.getActionsById(id);
        return CommonResult.success(actions);
    }

    @Operation(summary = "查询指定动作配置内容")
    @GetMapping("/{id}/action-value")
    public CommonResult<JsonNode> getActionValue(
            @PathVariable Long id,
            @RequestParam("actionName") @NotBlank(message = "动作名称不能为空") String actionName) {
        JsonNode actionValue = connectorService.getActionValueById(id, actionName);
        return CommonResult.success(actionValue);
    }

    @Operation(summary = "获取连接器的动作列表", description = "返回连接器的动作配置列表")
    @GetMapping("/{id}/action-infos")
    public CommonResult<List<ConnectorActionVO>> getActionInfos(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long id) {
        List<ConnectorActionVO> actions = connectorService.getActionInfos(id);
        return CommonResult.success(actions);
    }

    @Operation(summary = "获取动作详情")
    @GetMapping("/{connectorId}/actions/{actionId}")
    public CommonResult<ConnectorActionVO> getActionDetail(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        ConnectorActionVO action = connectorService.getActionDetail(connectorId, actionId);
        return CommonResult.success(action);
    }

    @Operation(summary = "保存动作草稿")
    @PostMapping("/{connectorId}/actions")
    public CommonResult<String> saveActionDraft(
            @PathVariable Long connectorId,
            @RequestBody @Valid CreateConnectorActionReqVO createVO) {
        String actionId = connectorService.saveActionDraft(connectorId, createVO);
        return CommonResult.success(actionId);
    }

    @Operation(summary = "更新动作草稿")
    @PutMapping("/{connectorId}/actions/{actionId}")
    public CommonResult<Boolean> updateActionDraft(
            @PathVariable Long connectorId,
            @PathVariable String actionId,
            @RequestBody @Valid UpdateConnectorActionReqVO updateVO) {
        connectorService.updateActionDraft(connectorId, actionId, updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "发布动作")
    @PutMapping("/{connectorId}/actions/{actionId}/publish")
    public CommonResult<Boolean> publishAction(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        connectorService.publishAction(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "下架动作")
    @PutMapping("/{connectorId}/actions/{actionId}/offline")
    public CommonResult<Boolean> offlineAction(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        connectorService.offlineAction(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "重新上线动作")
    @PutMapping("/{connectorId}/actions/{actionId}/republish")
    public CommonResult<Boolean> republishAction(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        connectorService.republishAction(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "复制动作")
    @PostMapping("/{connectorId}/actions/{actionId}/copy")
    public CommonResult<String> copyAction(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        String newActionId = connectorService.copyAction(connectorId, actionId);
        return CommonResult.success(newActionId);
    }

    @Operation(summary = "删除动作")
    @DeleteMapping("/{connectorId}/actions/{actionId}")
    public CommonResult<Boolean> deleteAction(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        connectorService.deleteAction(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "校验动作是否可发布")
    @PostMapping("/{connectorId}/actions/{actionId}/validate")
    public CommonResult<Boolean> validateActionForPublish(
            @PathVariable Long connectorId,
            @PathVariable String actionId) {
        ConnectorConfigHelper.ValidationResult result =
                connectorService.validateActionForPublish(connectorId, actionId);
        return CommonResult.success(result.isValid());
    }

    // ==================== 动作配置管理接口 v2.0 ====================

    @Operation(summary = "获取连接器的动作列表 v2.0", description = "返回四步配置 Schema + 使用次数")
    @GetMapping("/{id}/action-infos-v2")
    public CommonResult<List<ActionInfoVO>> getActionInfosV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long id) {
        List<ActionInfoVO> actions = connectorService.getActionInfosV2(id);
        return CommonResult.success(actions);
    }

    @Operation(summary = "获取动作详情 v2.0", description = "返回完整的四步配置 + 使用次数")
    @GetMapping("/{connectorId}/actions-v2/{actionId}")
    public CommonResult<ActionDetailVO> getActionDetailV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        ActionDetailVO action = connectorService.getActionDetailV2(connectorId, actionId);
        return CommonResult.success(action);
    }

    @Operation(summary = "保存动作草稿 v2.0", description = "使用四步配置 Schema 创建动作")
    @PostMapping("/{connectorId}/actions-v2")
    public CommonResult<String> saveActionDraftV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @RequestBody @Valid SaveActionReqVO reqVO) {
        String actionId = connectorService.saveActionDraftV2(connectorId, reqVO);
        return CommonResult.success(actionId);
    }

    @Operation(summary = "更新动作草稿 v2.0", description = "使用四步配置 Schema 更新动作")
    @PutMapping("/{connectorId}/actions-v2/{actionId}")
    public CommonResult<Boolean> updateActionDraftV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId,
            @RequestBody @Valid UpdateActionReqVO reqVO) {
        connectorService.updateActionDraftV2(connectorId, actionId, reqVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "发布动作 v2.0", description = "发布前会校验所有步骤配置是否完整")
    @PutMapping("/{connectorId}/actions-v2/{actionId}/publish")
    public CommonResult<Boolean> publishActionV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        connectorService.publishActionV2(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "下架动作 v2.0")
    @PutMapping("/{connectorId}/actions-v2/{actionId}/offline")
    public CommonResult<Boolean> offlineActionV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        connectorService.offlineActionV2(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "重新上线动作 v2.0")
    @PutMapping("/{connectorId}/actions-v2/{actionId}/republish")
    public CommonResult<Boolean> republishActionV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        connectorService.republishActionV2(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "复制动作 v2.0", description = "自动生成唯一名称：原名称_copy序号，复制的动作状态为 draft")
    @PostMapping("/{connectorId}/actions-v2/{actionId}/copy")
    public CommonResult<CopyActionRespVO> copyActionV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        CopyActionRespVO result = connectorService.copyActionV2(connectorId, actionId);
        return CommonResult.success(result);
    }

    @Operation(summary = "删除动作 v2.0", description = "删除前会检查是否被逻辑流引用")
    @DeleteMapping("/{connectorId}/actions-v2/{actionId}")
    public CommonResult<Boolean> deleteActionV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        connectorService.deleteActionV2(connectorId, actionId);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "校验动作是否可发布 v2.0", description = "返回包含完整的错误信息的校验结果")
    @PostMapping("/{connectorId}/actions-v2/{actionId}/validate")
    public CommonResult<ValidationResultVO> validateActionForPublishV2(
            @Parameter(description = "连接器实例ID", required = true, example = "1")
            @PathVariable Long connectorId,
            @Parameter(description = "动作ID", required = true, example = "action_123")
            @PathVariable String actionId) {
        ValidationResultVO result = connectorService.validateActionForPublishV2(connectorId, actionId);
        return CommonResult.success(result);
    }
}
