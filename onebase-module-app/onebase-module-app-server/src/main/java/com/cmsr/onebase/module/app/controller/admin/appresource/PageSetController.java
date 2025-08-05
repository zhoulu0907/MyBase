package com.cmsr.onebase.module.app.controller.admin.appresource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetVO;
import com.cmsr.onebase.module.app.service.appresource.PageSetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

@Tag(name = "应用资源管理-页面集管理")
@RestController
@RequestMapping("/app_resource/page_set")
@Validated
public class PageSetController {

    @Resource
    private PageSetService pageSetService;

    @PostMapping("/save")
    @Operation(summary = "保存页面集")
    public CommonResult<Boolean> savePageSet(@RequestBody SavePageSetVO savePageSetVO) {

        pageSetService.savePageSet(savePageSetVO);

        return CommonResult.success(true);
    }
}
