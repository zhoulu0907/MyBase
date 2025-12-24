package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.NodeConfigVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeConfigReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:01
 */
@Tag(name = "节点分类", description = "流程节点分类")
@RestController
@RequestMapping("/flow/node-type")
@Validated
@Deprecated
public class FlowNodeTypeController {

    @Operation(summary = "获取流程节点分类列表")
    @GetMapping("/page")
    public CommonResult<PageResult<NodeConfigVO>> pageNodeType(PageNodeConfigReqVO reqVO) {
        return CommonResult.success(null);
    }

}
