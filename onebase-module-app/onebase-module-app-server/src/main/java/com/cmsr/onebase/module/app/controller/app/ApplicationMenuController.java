package com.cmsr.onebase.module.app.controller.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;
import com.cmsr.onebase.module.app.service.app.ApplicationMenuService;
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
@Tag(name = "应用管理-菜单管理")
@RestController
@RequestMapping("/app/application-menu")
@Validated
public class ApplicationMenuController {

    @Resource
    private ApplicationMenuService applicationMenuService;

    @GetMapping("/list")
    @Operation(summary = "应用菜单列表")
    public CommonResult<List<ApplicationVersionListRespVO>> listApplicationMenu(@RequestParam("applicationId") Long applicationId) {
        return success(applicationMenuService.listApplicationMenu(applicationId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用菜单")
    public CommonResult<Long> createApplicationMenu(@RequestBody ApplicationMenuCreateReqVO applicationMenuCreateReqVO) {
        return success(applicationMenuService.createApplicationMenu(applicationMenuCreateReqVO));
    }

    @PutMapping("/update-name")
    @Operation(summary = "更新应用菜单名称")
    public CommonResult<Boolean> updateApplicationMenuName(@RequestParam("applicationId") Long applicationId,
                                                          @RequestParam("menuUuid") String menuUuid,
                                                          @RequestParam("menuName") String menuName) {
        applicationMenuService.updateApplicationMenuName(applicationId, menuUuid, menuName);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用菜单")
    public CommonResult<Boolean> deleteApplicationMenu(@RequestParam("applicationId") Long applicationId,
                                                          @RequestParam("menuUuid") String menuUuid) {
        applicationMenuService.deleteApplicationMenu(applicationId, menuUuid);
        return CommonResult.success(true);
    }

}
