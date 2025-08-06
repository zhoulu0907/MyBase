package com.cmsr.onebase.module.app.controller.admin.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageRespVO;
import com.cmsr.onebase.module.app.service.app.AppApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：huangjie
 * @Date：2025/7/22 14:48
 */
@Tag(name = "应用管理")
@RestController
@RequestMapping("/app/application")
@Validated
public class AppApplicationController {

    @Resource
    private AppApplicationService appApplicationService;


    @GetMapping("/page")
    @Operation(summary = "获得应用列表")
    public CommonResult<PageResult<ApplicationPageRespVO>> getApplicationPage(@Validated ApplicationPageReqVO pageReqVO) {
        return CommonResult.success(appApplicationService.getApplicationPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用")
    public CommonResult<ApplicationCreateRespVO> createApplication(@Validated @RequestBody ApplicationCreateReqVO applicationCreateReqVO) {
        return CommonResult.success(appApplicationService.createApplication(applicationCreateReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新应用")
    public CommonResult<Boolean> updateApplication(@Validated @RequestBody ApplicationCreateReqVO applicationCreateReqVO) {
        appApplicationService.updateApplication(applicationCreateReqVO);
        return CommonResult.success(true);
    }


    @PostMapping("/update-name")
    @Operation(summary = "更新应用名称")
    @Parameters({
            @Parameter(name = "id", description = "应用id", required = true),
            @Parameter(name = "name", description = "应用名称", required = true)
    })
    public CommonResult<Boolean> updateApplicationName(@RequestParam("id") Long id,
                                                       @RequestParam("name") String name) {
        appApplicationService.updateApplicationName(id, name);
        return CommonResult.success(true);
    }


    @PostMapping("/delete")
    @Operation(summary = "删除应用")
    @Parameters({
            @Parameter(name = "id", description = "应用id", required = true),
            @Parameter(name = "name", description = "应用名称", required = true)
    })
    public CommonResult<Boolean> deleteApplication(@RequestParam("id") Long id,
                                                   @RequestParam("name") String name) {
        appApplicationService.deleteApplication(id, name);
        return CommonResult.success(true);
    }
}
