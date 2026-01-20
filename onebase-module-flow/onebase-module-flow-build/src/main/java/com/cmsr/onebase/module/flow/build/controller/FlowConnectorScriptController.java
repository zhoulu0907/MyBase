package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorScriptService;
import com.cmsr.onebase.module.flow.build.vo.ConnectorScriptVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "连接器(脚本)", description = "脚本连接器动作接口")
@RestController
@RequestMapping("/flow/connector/script")
public class FlowConnectorScriptController {

    @Resource
    private FlowConnectorScriptService connectorScriptService;

    @Operation(summary = "分页查询脚本连接器动作")
    @GetMapping("/page")
    public CommonResult<PageResult<ConnectorScriptVO>> pageConnectorScripts(@Valid PageConnectorScriptReqVO pageReqVO) {
        log.info("[ConnectorScript] Page query requested: {}", pageReqVO);
        PageResult<ConnectorScriptVO> pageResult = connectorScriptService.pageConnectorScripts(pageReqVO);
        log.info("[ConnectorScript] Page query result: total={}, items={}",
            pageResult.getTotal(), pageResult.getList() != null ? pageResult.getList().size() : 0);
        return CommonResult.success(pageResult);
    }

    @Operation(summary = "获取脚本连接器动作详情")
    @GetMapping("/get")
    public CommonResult<ConnectorScriptVO> getConnectorScriptDetail(@RequestParam("id") Long scriptId) {
        log.info("[ConnectorScript] Get detail requested: id={}", scriptId);
        ConnectorScriptVO connectorScriptVO = connectorScriptService.getConnectorScript(scriptId);
        log.info("[ConnectorScript] Get detail result: found={}", connectorScriptVO != null);
        return CommonResult.success(connectorScriptVO);
    }

    @Operation(summary = "创建脚本连接器动作")
    @PostMapping("/create")
    public CommonResult<Long> createConnectorScript(@RequestBody @Valid CreateFlowConnectorScriptReqVO createVO) {
        log.info("[ConnectorScript] Create requested: scriptName={}", createVO.getScriptName());
        Long scriptId = connectorScriptService.createConnectorScript(createVO);
        log.info("[ConnectorScript] Create success: id={}", scriptId);
        return CommonResult.success(scriptId);
    }

    @Operation(summary = "更新脚本连接器动作")
    @PostMapping("/update")
    public CommonResult<Boolean> updateConnectorScript(@RequestBody @Valid UpdateFlowConnectorScriptReqVO updateVO) {
        log.info("[ConnectorScript] Update requested: id={}, scriptName={}", updateVO.getId(), updateVO.getScriptName());
        connectorScriptService.updateConnectorScript(updateVO);
        log.info("[ConnectorScript] Update success: id={}", updateVO.getId());
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除脚本连接器动作")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteConnectorScript(@RequestParam("id") Long scriptId) {
        log.info("[ConnectorScript] Delete requested: id={}", scriptId);
        connectorScriptService.deleteById(scriptId);
        log.info("[ConnectorScript] Delete success: id={}", scriptId);
        return CommonResult.success(Boolean.TRUE);
    }

}
