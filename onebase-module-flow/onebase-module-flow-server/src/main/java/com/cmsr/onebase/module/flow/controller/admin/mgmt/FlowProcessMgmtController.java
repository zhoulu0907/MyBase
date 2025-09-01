package com.cmsr.onebase.module.flow.controller.admin.mgmt;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.*;
import com.cmsr.onebase.module.flow.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.service.mgmt.FlowProcessMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程管理 - 管理员控制器
 */
@Setter
@RestController
@RequestMapping("/flow/mgmt")
@Tag(name = "流程管理", description = "流程管理相关接口")
@Validated
public class FlowProcessMgmtController {

    @Autowired
    private FlowProcessMgmtService flowProcessMgmtService;

    @GetMapping("/page")
    @Operation(summary = "分页查询流程列表")
    public CommonResult<PageResult<FlowProcessVO>> pageList(PageFlowProcessReqVO reqVO) {
        PageResult<FlowProcessVO> pageResult = flowProcessMgmtService.pageList(reqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("/get")
    @Operation(summary = "获取流程详情")
    public CommonResult<FlowProcessVO> getDetail(@RequestParam("id") Long id) {
        FlowProcessVO flowProcessVO = flowProcessMgmtService.getDetail(id);
        if (flowProcessVO == null) {
            return CommonResult.error(FlowErrorCodeConstants.FLOW_NOT_EXIST);
        }
        return CommonResult.success(flowProcessVO);
    }


    @PostMapping
    @Operation(summary = "创建流程")
    public CommonResult<Long> create(@RequestBody @Valid CreateFlowProcessReqVO reqVO) {
        Long id = flowProcessMgmtService.create(reqVO);
        return CommonResult.success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新流程")
    public CommonResult<Boolean> update(@RequestBody @Valid UpdateFlowProcessReqVO reqVO) {
        flowProcessMgmtService.update(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/rename")
    @Operation(summary = "重命名流程")
    public CommonResult<Boolean> renameFlowProcess(@RequestBody @Valid RenameFlowProcessReqVO reqVO) {
        flowProcessMgmtService.renameFlowProcess(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-definition")
    @Operation(summary = "更新流程定义")
    public CommonResult<Boolean> updateProcessDefinition(@RequestBody @Valid UpdateProcessDefinitionReqVO reqVO) {
        flowProcessMgmtService.updateProcessDefinition(reqVO);
        return CommonResult.success(true);
    }


    @PostMapping("/enable")
    @Operation(summary = "启用流程")
    public CommonResult<Boolean> enableFlowProcess(@RequestParam Long id) {
        flowProcessMgmtService.enableFlowProcess(id);
        return CommonResult.success(true);
    }

    @PostMapping("/disable")
    @Operation(summary = "关闭流程")
    public CommonResult<Boolean> disableFlowProcess(@RequestParam Long id) {
        flowProcessMgmtService.disableFlowProcess(id);
        return CommonResult.success(true);
    }


    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public CommonResult<Boolean> delete(@RequestParam Long id) {
        flowProcessMgmtService.delete(id);
        return CommonResult.success(true);
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除流程")
    public CommonResult<Boolean> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResult.success(true);
        }
        flowProcessMgmtService.batchDelete(ids);
        return CommonResult.success(true);
    }
}
