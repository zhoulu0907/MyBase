package com.cmsr.onebase.module.bpm.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowExecService;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowHisTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
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
 * 流程执行控制器
 */
@RestController
@RequestMapping("/bpm/exec")
@Tag(name = "流程执行", description = "流程执行相关接口")
@Slf4j
public class BpmFlowExecController {
    @Resource
    private BpmFlowExecService bpmFlowExecService;
    /**
     * 获取流程图
     *
     * @param flowCode 流程编码
     */
    @PostMapping("/get-flow-chart")
    @Operation(summary = "分页查询待办信息")
    //@PreAuthorize("@ss.hasPermission('bpm:engine:execute')")formula
    public CommonResult<PageResult<BpmFlowTodoTaskVO>> getFlowChart(@RequestParam @NotNull(message = "流程编码不能为空") String flowCode,
                                                            @RequestParam @NotNull(message = "应用ID不能为空") String appId) {
//        log.info("分页查询待办信息: {}", reqVO);
//        PageResult<BpmFlowTodoTaskVO> pageResult = flowTaskCenterService.getTodoPage(reqVO);
//        return success(pageResult);
        bpmFlowExecService.getFlowChart(flowCode, appId);

        return null;
    }

}
