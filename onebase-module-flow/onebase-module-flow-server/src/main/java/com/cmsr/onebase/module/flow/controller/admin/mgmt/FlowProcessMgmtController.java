package com.cmsr.onebase.module.flow.controller.admin.mgmt;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.CreateFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.FlowProcessVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.ListFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.UpdateFlowProcessReqVO;
import com.cmsr.onebase.module.flow.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.service.mgmt.FlowProcessMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程管理 - 管理员控制器
 */
@RestController
@RequestMapping("/admin/mgmt/flow-process")
@Tag(name = "流程管理", description = "流程管理相关接口")
public class FlowProcessMgmtController {

    private static final Logger logger = LoggerFactory.getLogger(FlowProcessMgmtController.class);

    @Autowired
    private FlowProcessMgmtService flowProcessMgmtService;

    @PostMapping("/page")
    @Operation(summary = "分页查询流程列表")
    public CommonResult<PageResult<FlowProcessVO>> pageList(@RequestBody @Validated ListFlowProcessReqVO reqVO) {
        logger.info("收到分页查询流程列表请求：{}", reqVO);
        PageResult<FlowProcessVO> pageResult = flowProcessMgmtService.pageList(reqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取流程详情")
    public CommonResult<FlowProcessVO> getDetail(@PathVariable Long id) {
        logger.info("收到获取流程详情请求，流程ID：{}", id);
        FlowProcessVO flowProcessVO = flowProcessMgmtService.getDetail(id);
        if (flowProcessVO == null) {
            return CommonResult.error(FlowErrorCodeConstants.FLOW_NOT_EXIST);
        }
        return CommonResult.success(flowProcessVO);
    }

    @PostMapping
    @Operation(summary = "创建流程")
    public CommonResult<Long> create(@RequestBody @Validated CreateFlowProcessReqVO reqVO) {
        logger.info("收到创建流程请求：{}", reqVO);
        Long id = flowProcessMgmtService.create(reqVO);
        return CommonResult.success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新流程")
    public CommonResult<Boolean> update(@RequestBody @Validated UpdateFlowProcessReqVO reqVO) {
        logger.info("收到更新流程请求：{}", reqVO);
        flowProcessMgmtService.update(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public CommonResult<Boolean> delete(@RequestParam Long id) {
        logger.info("收到删除流程请求，流程ID：{}", id);
        flowProcessMgmtService.delete(id);
        return CommonResult.success(true);
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除流程")
    public CommonResult<Boolean> batchDelete(@RequestBody List<Long> ids) {
        logger.info("收到批量删除流程请求，流程ID列表：{}", ids);
        if (ids == null || ids.isEmpty()) {
            return CommonResult.success(true);
        }
        flowProcessMgmtService.batchDelete(ids);
        return CommonResult.success(true);
    }
}
