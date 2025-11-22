package com.cmsr.onebase.module.app.build.controller.menu;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.menu.AppMenuService;
import com.cmsr.onebase.module.app.build.vo.menu.*;
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
public class AppMenuController {

    @Resource
    private AppMenuService appMenuService;

    @GetMapping("/bpm-list")
    @Operation(summary = "BPM应用菜单列表")
    public CommonResult<List<MenuListRespVO>> listBpmApplicationMenu(
            @RequestParam("applicationId") Long applicationId) {
        return success(appMenuService.listBpmApplicationMenu(applicationId));
    }

    @GetMapping("/list")
    @Operation(summary = "应用菜单列表")
    public CommonResult<List<MenuListRespVO>> listApplicationMenu(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam(name = "name", required = false) String name) {
        return success(appMenuService.listApplicationMenu(applicationId, name));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用菜单")
    public CommonResult<MenuCreateRespVO> createApplicationMenu(@RequestBody MenuCreateReqVO createReqVO) {
        return success(appMenuService.createApplicationMenu(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新应用菜单")
    public CommonResult<Boolean> updateApplicationMenu(@RequestBody MenuUpdateReqVO updateReqVO) {
        appMenuService.updateApplicationMenu(updateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-name")
    @Operation(summary = "更新应用菜单名称")
    public CommonResult<Boolean> updateApplicationMenuName(@RequestParam("id") Long id,
                                                           @RequestParam("menuName") String menuName) {
        appMenuService.updateApplicationMenuName(id, menuName);
        return CommonResult.success(true);
    }

    @PostMapping("/update-order")
    @Operation(summary = "更新应用菜单排序")
    public CommonResult<Boolean> updateApplicationMenuOrder(@RequestBody MenuOrderUpdateReqVO updateReqVO) {
        appMenuService.updateApplicationMenuOrder(updateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-visible")
    @Operation(summary = "更新应用菜单可见性")
    public CommonResult<Boolean> updateApplicationMenuVisible(@RequestParam("id") Long id,
                                                              @RequestParam("visible") Integer visible) {
        appMenuService.updateApplicationMenuVisible(id, visible);
        return CommonResult.success(true);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制应用菜单")
    public CommonResult<MenuCreateRespVO> copyApplicationMenu(@RequestBody MenuCopyReqVO copyReqVO) {
        return CommonResult.success(appMenuService.copyApplicationMenu(copyReqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用菜单")
    public CommonResult<Boolean> deleteApplicationMenu(@RequestParam("id") Long id) {
        appMenuService.deleteApplicationMenu(id);
        return CommonResult.success(true);
    }

}
