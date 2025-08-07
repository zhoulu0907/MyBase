package com.cmsr.onebase.module.app.controller.admin.version;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionListRespVO;
import com.cmsr.onebase.module.app.service.version.AppVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:32
 */
@Tag(name = "应用管理-版本管理")
@RestController
@RequestMapping("/app/version")
@Validated
public class AppVersionController {

    @Resource
    private AppVersionService appVersionService;

    @GetMapping("/list")
    @Operation(summary = "应用版本列表")
    public CommonResult<List<VersionListRespVO>> listApplicationVersion(@RequestParam("applicationId") Long applicationId) {
        return CommonResult.success(appVersionService.listApplicationVersion(applicationId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用版本")
    public CommonResult<Boolean> createApplicationVersion(@RequestBody VersionCreateReqVO createReqVO) {
        appVersionService.createApplicationVersion(createReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/restore")
    @Operation(summary = "应用版本启用")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> restoreApplicationVersion(@RequestParam("versionId") Long versionId) {
        appVersionService.restoreApplicationVersion(versionId);
        return CommonResult.success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用版本")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> deleteApplicationVersion(@RequestParam("versionId") Long versionId) {
        appVersionService.deleteApplicationVersion(versionId);
        return CommonResult.success(true);
    }

}
