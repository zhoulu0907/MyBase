package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowNodeTypeService;
import com.cmsr.onebase.module.flow.build.vo.NodeTypeVO;
import com.cmsr.onebase.module.flow.core.vo.PageNodeTypeReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
public class FlowNodeTypeController {

    @Resource
    private FlowNodeTypeService flowNodeTypeService;

    @GetMapping("/page")
    @Operation(summary = "获取流程节点分类列表")
    public CommonResult<PageResult<NodeTypeVO>> pageNodeType(PageNodeTypeReqVO reqVO) {
        PageResult<NodeTypeVO> result = flowNodeTypeService.pageNodeType(reqVO);
        return CommonResult.success(result);
    }

}
