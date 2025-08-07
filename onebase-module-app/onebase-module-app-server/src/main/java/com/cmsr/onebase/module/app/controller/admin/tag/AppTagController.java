package com.cmsr.onebase.module.app.controller.admin.tag;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.tag.vo.CreateTagReqVO;
import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagRespVO;
import com.cmsr.onebase.module.app.service.tag.AppTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:32
 */
@Tag(name = "应用管理-标签管理")
@RestController
@RequestMapping("/app/tag")
@Validated
public class AppTagController {

    @Resource
    private AppTagService appTagService;

    @GetMapping("/list")
    @Operation(summary = "应用标签列表")
    public CommonResult<List<TagRespVO>> listTag(@RequestParam(name = "tagName", required = false) String tagName) {
        return success(appTagService.listTags(tagName));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用标签")
    public CommonResult<Boolean> createTag(@RequestBody CreateTagReqVO createTagReqVO) {
        appTagService.createTag(createTagReqVO.getTagName());
        return success(true);
    }

}
