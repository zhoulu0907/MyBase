package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowHisTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 流程任务中心
 */
@RestController
@RequestMapping("/bpm/task-center")
@Tag(name = "流程任务中心", description = "流程任务中心相关接口")
@Slf4j
public class BpmFlowTaskCenterController {
    @Resource
    private BpmFlowTaskCenterService flowTaskCenterService;
    @GetMapping("/todo/page")
    @Operation(summary = "分页查询待办信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmFlowTodoTaskVO>> todo(@Valid BpmFlowTodoTaskPageReqVO reqVO) {
        log.info("分页查询待办信息: {}", reqVO);
        PageResult<BpmFlowTodoTaskVO> pageResult = flowTaskCenterService.getTodoPage(reqVO);
        return success(pageResult);
    }

    @GetMapping("/done/page")
    @Operation(summary = "分页查询已办信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmFlowDoneTaskVO>> done(@Valid BpmFlowDoneTaskPageReqVO reqVO) {
        log.info("分页查询已办信息: {}", reqVO);
        PageResult<BpmFlowDoneTaskVO> pageResult = flowTaskCenterService.getDonePage(reqVO);
        return success(pageResult);
    }

    @GetMapping("/my-create/page")
    @Operation(summary = "分页查询我创建的流程信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmMyCreatedVO>> myCreate(@Valid BpmMyCreatedPageReqVO reqVO) {
        log.info("分页查询我创建的流程信息: {}", reqVO);
        PageResult<BpmMyCreatedVO> pageResult = bpmFlowTodoRuntimeService.getMyCreatedPage(reqVO);
        return success(pageResult);
    }

    @PostMapping("/his/get")
    @Operation(summary = "查询流程历史信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<List<BpmFlowHisTaskVO>> getHisTask(@RequestParam @NotNull(message = "实例ID不能为空") Long instanceId) {
        log.info("查询流程历史信息实例ID: {}", instanceId);
        List<FlowHisTask> list = flowTaskCenterService.getHisTaskByInstanceId(instanceId);
        return success(BeanUtils.toBean(list,BpmFlowHisTaskVO.class));
    }
}
