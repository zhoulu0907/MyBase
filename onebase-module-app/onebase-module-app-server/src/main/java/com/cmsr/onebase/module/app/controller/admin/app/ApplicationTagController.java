package com.cmsr.onebase.module.app.controller.admin.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationTagListRespVO;
import com.cmsr.onebase.module.app.service.app.ApplicationTagService;
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
@RequestMapping("/app/application-tag")
@Validated
public class ApplicationTagController {

    @Resource
    private ApplicationTagService applicationTagService;

    @GetMapping("/list")
    @Operation(summary = "应用标签列表")
    public CommonResult<List<ApplicationTagListRespVO>> listApplicationTag(@RequestParam("tagName") String tagName) {
        return success(applicationTagService.listApplicationTags(tagName));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用标签")
    public CommonResult<Boolean> createApplicationTag(@RequestParam("tagName") String tagName) {
        applicationTagService.createApplicationTag(tagName);
        return success(true);
    }

}
