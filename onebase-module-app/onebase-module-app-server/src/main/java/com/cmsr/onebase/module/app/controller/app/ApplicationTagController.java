package com.cmsr.onebase.module.app.controller.app;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationTagListRespVO;
import com.cmsr.onebase.module.app.service.app.ApplicationTagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

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
    public CommonResult<List<ApplicationTagListRespVO>> listApplicationTag(@RequestParam("tenantId") Long tenantId, @RequestParam("query") String query) {
        return success(applicationTagService.listApplicationTags(tenantId, query));
    }

}
