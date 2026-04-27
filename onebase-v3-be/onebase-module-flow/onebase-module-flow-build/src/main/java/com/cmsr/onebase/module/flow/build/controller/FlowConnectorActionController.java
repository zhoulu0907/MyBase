package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorActionService;
import com.cmsr.onebase.module.flow.build.vo.CreateActionReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateActionReqVO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorActionDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 连接器动作控制器（统一动作表）
 * <p>
 * 提供动作的 CRUD 接口，所有连接器类型的动作都通过此接口管理
 *
 * @author onebase
 * @since 2026-03-19
 */
@Tag(name = "连接器动作", description = "连接器动作接口（统一动作表）")
@RestController
@RequestMapping("/flow/connector-action")
@Validated
public class FlowConnectorActionController {

    @Resource
    private FlowConnectorActionService actionService;

    @Operation(summary = "创建动作", description = "创建新的连接器动作")
    @PostMapping("/create")
    public CommonResult<Long> createAction(@RequestBody @Valid CreateActionReqVO createReqVO) {
        Long actionId = actionService.createAction(createReqVO);
        return CommonResult.success(actionId);
    }

    @Operation(summary = "更新动作", description = "更新已存在的动作配置")
    @PostMapping("/update")
    public CommonResult<Boolean> updateAction(@RequestBody @Valid UpdateActionReqVO updateReqVO) {
        actionService.updateAction(updateReqVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除动作", description = "软删除动作（设置 activeStatus=0）")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteAction(@RequestBody @Valid ActionIdReqVO reqVO) {
        actionService.deleteAction(reqVO.getId());
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "获取动作详情", description = "根据动作ID获取详情")
    @GetMapping("/detail")
    public CommonResult<FlowConnectorActionDO> getAction(
            @Parameter(description = "动作ID", required = true, example = "1")
            @RequestParam("id") Long id) {
        FlowConnectorActionDO action = actionService.getAction(id);
        return CommonResult.success(action);
    }

    @Operation(summary = "根据动作UUID获取详情", description = "通过 actionUuid 获取动作详情")
    @GetMapping("/detail-by-uuid")
    public CommonResult<FlowConnectorActionDO> getActionByUuid(
            @Parameter(description = "动作UUID", required = true, example = "action-abc12345")
            @RequestParam("actionUuid") String actionUuid) {
        FlowConnectorActionDO action = actionService.getActionByUuid(actionUuid);
        return CommonResult.success(action);
    }

    @Operation(summary = "根据连接器UUID获取动作列表", description = "获取指定连接器的所有动作")
    @GetMapping("/list")
    public CommonResult<List<FlowConnectorActionDO>> getActionsByConnectorUuid(
            @Parameter(description = "连接器UUID", required = true, example = "conn-abc123")
            @RequestParam("connectorUuid") String connectorUuid) {
        List<FlowConnectorActionDO> actions = actionService.getActionsByConnectorUuid(connectorUuid);
        return CommonResult.success(actions);
    }

    @Operation(summary = "根据连接器UUID和动作编码获取动作", description = "精确查找指定连接器下的特定动作")
    @GetMapping("/detail-by-code")
    public CommonResult<FlowConnectorActionDO> getActionByConnectorUuidAndCode(
            @Parameter(description = "连接器UUID", required = true, example = "conn-abc123")
            @RequestParam("connectorUuid") String connectorUuid,
            @Parameter(description = "动作编码", required = true, example = "GET_USER_INFO")
            @RequestParam("actionCode") String actionCode) {
        FlowConnectorActionDO action = actionService.getActionByConnectorUuidAndCode(connectorUuid, actionCode);
        return CommonResult.success(action);
    }

    @Operation(summary = "分页查询动作列表", description = "分页获取动作列表，可按连接器UUID筛选")
    @GetMapping("/page")
    public CommonResult<PageResult<FlowConnectorActionDO>> getActionPage(
            @Parameter(description = "连接器UUID（可选）", example = "conn-abc123")
            @RequestParam(value = "connectorUuid", required = false) String connectorUuid,
            @Parameter(description = "页码", example = "1")
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<FlowConnectorActionDO> page = actionService.getActionPage(connectorUuid, pageNo, pageSize);
        return CommonResult.success(page);
    }

    @Operation(summary = "启用/禁用动作", description = "更新动作的启用状态")
    @PostMapping("/update-status")
    public CommonResult<Boolean> updateStatus(@RequestBody @Valid UpdateActionStatusReqVO reqVO) {
        UpdateActionReqVO updateReqVO = new UpdateActionReqVO();
        updateReqVO.setId(reqVO.getId());
        updateReqVO.setActiveStatus(reqVO.getActiveStatus());
        actionService.updateAction(updateReqVO);
        return CommonResult.success(Boolean.TRUE);
    }

    // ==================== 内部请求 VO ====================

    /**
     * 动作ID请求VO
     */
    @Data
    public static class ActionIdReqVO implements Serializable {
        @NotNull(message = "动作ID不能为空")
        private Long id;
    }

    /**
     * 更新动作状态请求VO
     */
    @Data
    public static class UpdateActionStatusReqVO implements Serializable {
        @NotNull(message = "动作ID不能为空")
        private Long id;

        @NotNull(message = "启用状态不能为空")
        private Integer activeStatus;
    }
}