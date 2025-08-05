package com.cmsr.onebase.module.app.controller.admin.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuCopyReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuGroupCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuListRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuOrderUpdateReqVO;
import com.cmsr.onebase.module.app.service.app.MenuService;
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
@RequestMapping("/app/menu")
@Validated
public class MenuController {

    @Resource
    private MenuService menuService;

    @GetMapping("/list")
    @Operation(summary = "应用菜单列表")
    public CommonResult<List<ApplicationMenuListRespVO>> listApplicationMenu(@RequestParam("applicationId") Long applicationId) {
        return success(menuService.listApplicationMenu(applicationId));
    }

    @PostMapping("/create-group")
    @Operation(summary = "创建应用菜单分组")
    public CommonResult<Long> createApplicationMenuGroup(@RequestBody ApplicationMenuGroupCreateReqVO createReqVO) {
        return success(menuService.createApplicationMenuGroup(createReqVO));
    }

    @PostMapping("/update-name")
    @Operation(summary = "更新应用菜单名称")
    public CommonResult<Boolean> updateApplicationMenuName(@RequestParam("id") Long id,
                                                           @RequestParam("menuName") String menuName) {
        menuService.updateApplicationMenuName(id, menuName);
        return CommonResult.success(true);
    }

    @PostMapping("/update-order")
    @Operation(summary = "更新应用菜单排序")
    public CommonResult<Boolean> updateApplicationMenuOrder(@RequestBody ApplicationMenuOrderUpdateReqVO updateReqVO) {
        menuService.updateApplicationMenuOrder(updateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-visible")
    @Operation(summary = "更新应用菜单可见性")
    public CommonResult<Boolean> updateApplicationMenuVisible(@RequestParam("id") Long id,
                                                              @RequestParam("visible") Boolean visible) {
        menuService.updateApplicationMenuVisible(id, visible);
        return CommonResult.success(true);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制应用菜单")
    public CommonResult<Boolean> copyApplicationMenu(@RequestBody ApplicationMenuCopyReqVO copyReqVO) {
        menuService.copyApplicationMenu(copyReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用菜单")
    public CommonResult<Boolean> deleteApplicationMenu(@RequestParam("id") Long id) {
        menuService.deleteApplicationMenu(id);
        return CommonResult.success(true);
    }

}
