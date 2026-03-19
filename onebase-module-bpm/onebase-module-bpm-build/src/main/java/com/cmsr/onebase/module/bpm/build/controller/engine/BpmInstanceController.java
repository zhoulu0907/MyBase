package com.cmsr.onebase.module.bpm.build.controller.engine;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.core.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.core.vo.instance.BpmSubmitReqVO;
import com.cmsr.onebase.module.bpm.core.vo.instance.BpmSubmitRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 流程实例管理Controller
 *
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "流程实例管理")
@RestController
@RequestMapping("/bpm/instance")
@Validated
@Slf4j
public class BpmInstanceController {

    @Resource
    private BpmInstanceService bpmInstanceService;

    @PostMapping("/submit")
    @Operation(summary = "流程发起")
    public CommonResult<BpmSubmitRespVO> exec(@RequestBody @Valid BpmSubmitReqVO reqVO) {
        log.info("执行流程实例的操作按钮: {}", reqVO);
        BpmSubmitRespVO respVO = bpmInstanceService.submit(reqVO);
        return CommonResult.success(respVO);
    }
}