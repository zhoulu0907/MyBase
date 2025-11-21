package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorService;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "连接器", description = "连接器接口")
@RestController
@RequestMapping("/flow/connector")
@Validated
public class FlowConnectorController {

    @Resource
    private FlowConnectorService connectorService;

    @Operation(summary = "分页查询连接器")
    @GetMapping("/page")
    public CommonResult<PageResult<FlowConnectorVO>> pageQuery(@Valid PageConnectorReqVO pageConnectorReqVO) {
        PageResult<FlowConnectorVO> connectorPage = connectorService.getConnectorPage(pageConnectorReqVO);
        return CommonResult.success(connectorPage);
    }

    @Operation(summary = "获取连接器详情")
    @GetMapping("/get")
    public CommonResult<FlowConnectorVO> getConnector(@RequestParam("id") Long connectorId) {
        FlowConnectorVO connectorDetail = connectorService.getConnectorDetail(connectorId);
        return CommonResult.success(connectorDetail);
    }

    @Operation(summary = "创建连接器")
    @PostMapping("/create")
    public CommonResult<Long> createConnectorBrief(@RequestBody @Valid CreateFlowConnectorReqVO createVO) {
        Long connectorId = connectorService.createConnector(createVO);
        return CommonResult.success(connectorId);
    }

    @Operation(summary = "更新连接器")
    @PostMapping("/update")
    public CommonResult<Boolean> updateConnectorBreif(@RequestBody @Valid UpdateFlowConnectorReqVO updateVO) {
        connectorService.updateConnector(updateVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除连接器")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteConnector(@RequestParam("id") Long connectorId) {
        connectorService.deleteById(connectorId);
        return CommonResult.success(Boolean.TRUE);
    }
}
