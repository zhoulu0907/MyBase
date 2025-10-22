package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTodoRuntimeService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 流程运行
 */
@RestController
@RequestMapping("/bpm/design")
@Tag(name = "流程运行", description = "流程运行相关接口")
@Slf4j
public class BpmFlowRuntimeController {
    @Resource
    private BpmFlowTodoRuntimeService bpmFlowTodoRuntimeService;
    @GetMapping("/todo/page")
    @Operation(summary = "分页查询待办信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmFlowTodoTaskVO>> todo(@Valid BpmFlowTodoTaskPageReqVO reqVO) {
        log.info("分页查询待办信息: {}", reqVO);
        PageResult<BpmFlowTodoTaskVO> pageResult = bpmFlowTodoRuntimeService.getTodoPage(reqVO);
        return success(pageResult);
    }
    @GetMapping("/done/page")
    @Operation(summary = "分页查询已办信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmFlowDoneTaskVO>> done(@Valid BpmFlowDoneTaskPageReqVO reqVO) {
        log.info("分页查询已办信息: {}", reqVO);
        PageResult<BpmFlowDoneTaskVO> pageResult = bpmFlowTodoRuntimeService.getTodoPage(reqVO);
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
}
