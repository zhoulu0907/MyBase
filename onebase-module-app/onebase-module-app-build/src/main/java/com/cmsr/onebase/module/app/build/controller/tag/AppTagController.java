package com.cmsr.onebase.module.app.build.controller.tag;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.tag.AppTagService;
import com.cmsr.onebase.module.app.build.vo.tag.CreateTagReqVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:32
 */
@Tag(name = "应用管理-标签管理")
@Setter
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

    @GetMapping("/group-count")
    @Operation(summary = "应用标签分组统计")
    public CommonResult<List<TagGroupCountVO>> groupCount() {
        return success(appTagService.groupCount());
    }

    @PostMapping("/update-tags")
    @Operation(summary = "更新应用标签集合")
    public CommonResult<Boolean> updateTags(@RequestBody List<TagRespVO> tagRespVOS) {
        appTagService.updateTags(tagRespVOS);
        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用标签")
    public CommonResult<Boolean> createTag(@RequestBody CreateTagReqVO createTagReqVO) {
        appTagService.createTag(createTagReqVO.getTagName());
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用标签")
    public CommonResult<Boolean> deleteTag(@RequestParam(name = "tagId") Long tagId) {
        appTagService.deleteTag(tagId);
        return success(true);
    }

}
