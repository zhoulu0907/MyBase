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
import org.springframework.web.bind.annotation.*;

@Tag(name = "连接器(脚本)", description = "脚本连接器动作接口")
@RestController
@RequestMapping("/flow/connector/script")
public class FlowConnectorScriptController {

    @Resource
    private FlowConnectorScriptService connectorScriptService;

    @Operation(summary = "分页查询脚本连接器动作")
    @GetMapping("/page")
    public CommonResult<PageResult<ConnectorScriptVO>> getConnectorScriptPage(@Valid PageConnectorScriptReqVO pageReqVO) {
        PageResult<ConnectorScriptVO> pageResult = connectorScriptService.getConnectorScriptPage(pageReqVO);
        return CommonResult.success(pageResult);
    }

    @Operation(summary = "获取脚本连接器动作详情")
    @GetMapping("/get")
    public CommonResult<ConnectorScriptVO> getConnectorScriptDetail(@RequestParam("id") Long scriptId) {
        ConnectorScriptVO connectorScriptVO = connectorScriptService.getConnectorScript(scriptId);
        return CommonResult.success(connectorScriptVO);
    }

    @Operation(summary = "创建脚本连接器动作")
    @PostMapping("/create")
    public CommonResult<Long> createConnectorScript(@RequestBody @Valid CreateFlowConnectorScriptReqVO createVO) {
        Long scriptId = connectorScriptService.createConnectorScript(createVO);
        return CommonResult.success(scriptId);
    }

    @Operation(summary = "更新脚本连接器动作")
    @PostMapping("/update")
    public CommonResult<Boolean> updateConnectorScript(@RequestBody @Valid UpdateFlowConnectorScriptReqVO updateVO) {
        connectorScriptService.updateConnectorScript(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除脚本连接器动作")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteConnectorScript(@RequestParam("id") Long scriptId) {
        connectorScriptService.deleteById(scriptId);
        return CommonResult.success(Boolean.TRUE);
    }

}
