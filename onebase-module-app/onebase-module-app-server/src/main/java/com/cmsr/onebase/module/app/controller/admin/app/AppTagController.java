package com.cmsr.onebase.module.app.controller.admin.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.app.vo.CreateTagReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ListTagReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.TagListRespVO;
import com.cmsr.onebase.module.app.service.app.AppTagService;
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
    public CommonResult<List<TagListRespVO>> listTag(@RequestBody ListTagReqVO listTagReqVO) {
        return success(appTagService.listTags(listTagReqVO.getTagName()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用标签")
    public CommonResult<Boolean> createTag(@RequestBody CreateTagReqVO createTagReqVO) {
        appTagService.createTag(createTagReqVO.getTagName());
        return success(true);
    }

}
