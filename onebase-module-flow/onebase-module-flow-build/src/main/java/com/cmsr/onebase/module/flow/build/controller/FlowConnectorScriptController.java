package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorScriptService;
import com.cmsr.onebase.module.flow.build.vo.ConnectorScriptVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorScriptReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow/connector/script")
public class FlowConnectorScriptController {

    @Resource
    private FlowConnectorScriptService connectorScriptService;

    @GetMapping("/page")
    public CommonResult<PageResult<ConnectorScriptVO>> getConnectorScriptPage(@Valid PageConnectorScriptReqVO pageReqVO) {
        PageResult<ConnectorScriptVO> pageResult = connectorScriptService.getConnectorScriptPage(pageReqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("/get")
    public CommonResult<ConnectorScriptVO> getConnectorScriptDetail(@RequestParam("id") Long scriptId) {
        ConnectorScriptVO connectorScriptVO = connectorScriptService.getConnectorScript(scriptId);
        return CommonResult.success(connectorScriptVO);
    }

    @PostMapping("/create")
    public CommonResult<Long> createConnectorScript(@RequestBody @Valid CreateFlowConnectorScriptReqVO createVO) {
        Long scriptId = connectorScriptService.createConnectorScript(createVO);
        return CommonResult.success(scriptId);
    }

    @PostMapping("/update")
    public CommonResult<Boolean> updateConnectorScript(@RequestBody @Valid UpdateFlowConnectorScriptReqVO updateVO) {
        connectorScriptService.updateConnectorScript(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    public CommonResult<Boolean> deleteConnectorScript(@RequestParam("id") Long scriptId) {
        connectorScriptService.deleteById(scriptId);
        return CommonResult.success(Boolean.TRUE);
    }

}
