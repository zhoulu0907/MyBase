package com.cmsr.onebase.module.app.controller.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;
import com.cmsr.onebase.module.app.service.app.ApplicationVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "应用管理-版本管理")
@RestController
@RequestMapping("/app/application-version")
@Validated
public class ApplicationVersionController {

    @Resource
    private ApplicationVersionService applicationVersionService;

    @GetMapping("/list")
    @Operation(summary = "应用版本列表")
    public CommonResult<List<ApplicationVersionListRespVO>> listApplicationVersion(@RequestParam("applicationId") Long applicationId) {
        return CommonResult.success(applicationVersionService.listApplicationVersion(applicationId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用版本")
    public CommonResult<Boolean> createApplicationVersion(@RequestBody ApplicationVersionCreateReqVO createReqVO) {
        applicationVersionService.createApplicationVersion(createReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/restore")
    @Operation(summary = "应用版本启用")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> restoreApplicationVersion(@RequestParam("versionId") Long versionId) {
        applicationVersionService.restoreApplicationVersion(versionId);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用版本")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> deleteApplicationVersion(@RequestParam("versionId") Long versionId) {
        applicationVersionService.deleteApplicationVersion(versionId);
        return CommonResult.success(true);
    }

}
