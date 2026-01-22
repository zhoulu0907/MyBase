package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.api.vo.NodeInfoVO;
import com.cmsr.onebase.module.flow.build.service.FlowNodeConfigService;
import com.cmsr.onebase.module.flow.build.vo.ConnectorTypeListVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigActionVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigConnVO;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:01
 */
@Tag(name = "连接器配置", description = "连接器节点配置")
@RestController
@RequestMapping("/flow/node-config")
@Validated
public class FlowNodeConfigController {

    @Resource
    private FlowNodeConfigService flowNodeConfigService;

    @Operation(summary = "获取连接器节点配置列表")
    @GetMapping("/page")
    public CommonResult<PageResult<NodeConfigVO>> pageNodeType(PageNodeConfigReqVO reqVO) {
        PageResult<NodeConfigVO> result = flowNodeConfigService.pageNodeType(reqVO);
        return CommonResult.success(result);
    }

    @Operation(summary = "根据node code获取conn配置类型和参数")
    @GetMapping("/get-conn-config")
    public CommonResult<NodeConfigConnVO> getConnConfig(@RequestParam("nodeCode") String nodeCode) {
        NodeConfigConnVO result = flowNodeConfigService.findConnConfig(nodeCode);
        return CommonResult.success(result);
    }

    @Operation(summary = "根据node code获取action配置类型和参数")
    @GetMapping("/get-action-config")
    public CommonResult<NodeConfigActionVO> getActionConfig(@RequestParam("nodeCode") String nodeCode) {
        NodeConfigActionVO result = flowNodeConfigService.findActionConfig(nodeCode);
        return CommonResult.success(result);
    }

    @Operation(summary = "获取连接器类型清单")
    @GetMapping("/list-all")
    public CommonResult<List<ConnectorTypeListVO>> listAllConnectorTypes() {
        List<ConnectorTypeListVO> result = flowNodeConfigService.getAllConnectorTypes();
        return CommonResult.success(result);
    }

    @Operation(summary = "查询所有连接器类型信息")
    @GetMapping("/node-types")
    public CommonResult<List<NodeInfoVO>> getAllNodeTypes() {
        List<NodeInfoVO> result = flowNodeConfigService.getAllNodeTypes();
        return CommonResult.success(result);
    }

}
