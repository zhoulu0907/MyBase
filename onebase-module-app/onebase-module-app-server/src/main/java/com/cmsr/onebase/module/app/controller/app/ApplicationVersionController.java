package com.cmsr.onebase.module.app.controller.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;
import com.cmsr.onebase.module.app.service.app.ApplicationVersionService;
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
        return success(applicationVersionService.listApplicationVersion(applicationId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用版本")
    public CommonResult<Long> createApplicationVersion(@RequestBody ApplicationVersionCreateReqVO applicationVersionCreateReqVO) {
        return success(applicationVersionService.createApplicationVersion(applicationVersionCreateReqVO));
    }

    @PostMapping("/turn-on")
    @Operation(summary = "应用版本启用")
    public CommonResult<Boolean> turnOnApplicationVersion(@RequestParam("applicationId") Long applicationId,
                                                          @RequestParam("versionNumber") String versionNumber) {
        applicationVersionService.turnOnApplicationVersion(applicationId, versionNumber);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用版本")
    public CommonResult<Boolean> deleteApplicationVersion(@RequestParam("applicationId") Long applicationId,
                                                          @RequestParam("versionNumber") String versionNumber) {
        applicationVersionService.deleteApplicationVersion(applicationId, versionNumber);
        return CommonResult.success(true);
    }

}
