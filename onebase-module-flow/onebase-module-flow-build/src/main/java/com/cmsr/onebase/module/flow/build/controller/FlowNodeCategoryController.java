package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.build.service.FlowNodeCategoryService;
import com.cmsr.onebase.module.flow.build.vo.NodeCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:01
 */
@Tag(name = "连接器分类", description = "流程连接器分类")
@RestController
@RequestMapping("/flow/node-category")
@Validated
public class FlowNodeCategoryController {

    @Resource
    private FlowNodeCategoryService flowNodeCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取连接器分类列表")
    public CommonResult<List<NodeCategoryVO>> getNodeCategoryList() {
        List<NodeCategoryVO> nodeCategoryVOS = flowNodeCategoryService.getNodeCategoryList();
        return CommonResult.success(nodeCategoryVOS);
    }

}
