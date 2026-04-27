package com.cmsr.onebase.module.app.build.controller.menu;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.menu.BuildAppMenuService;
import com.cmsr.onebase.module.app.build.vo.menu.*;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
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
    private BuildAppMenuService buildAppMenuService;

    @GetMapping("/bpm-list")
    @Operation(summary = "BPM应用菜单列表")
    public CommonResult<List<MenuListRespVO>> listBpmApplicationMenu(
            @RequestParam("applicationId") Long applicationId) {
        return success(buildAppMenuService.listBpmApplicationMenu(applicationId));
    }

    @GetMapping("/list")
    @Operation(summary = "应用菜单列表")
    public CommonResult<List<MenuListRespVO>> listApplicationMenu(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam(name = "name", required = false) String name) {
        return success(buildAppMenuService.listApplicationMenu(applicationId, name));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用菜单")
    public CommonResult<MenuCreateRespVO> createApplicationMenu(@RequestBody MenuCreateReqVO createReqVO) {
        return success(buildAppMenuService.createApplicationMenu(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新应用菜单")
    public CommonResult<Boolean> updateApplicationMenu(@RequestBody MenuUpdateReqVO updateReqVO) {
        buildAppMenuService.updateApplicationMenu(updateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-name")
    @Operation(summary = "更新应用菜单名称")
    public CommonResult<Boolean> updateApplicationMenuName(@RequestParam("id") Long id,
                                                           @RequestParam("menuName") String menuName) {
        buildAppMenuService.updateApplicationMenuName(id, menuName);
        return CommonResult.success(true);
    }

    @PostMapping("/update-order")
    @Operation(summary = "更新应用菜单排序")
    public CommonResult<Boolean> updateApplicationMenuOrder(@RequestBody MenuOrderUpdateReqVO updateReqVO) {
        buildAppMenuService.updateApplicationMenuOrder(updateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-visible-pc")
    @Operation(summary = "更新应用菜单可见性")
    public CommonResult<Boolean> updateApplicationMenuVisiblePc(@RequestParam("id") Long id,
                                                              @RequestParam("visible") Integer visible) {
        buildAppMenuService.updateApplicationMenuVisiblePc(id, visible);
        return CommonResult.success(true);
    }

    @PostMapping("/update-visible-mobile")
    @Operation(summary = "更新应用菜单可见性")
    public CommonResult<Boolean> updateApplicationMenuVisibleMobile(@RequestParam("id") Long id,
                                                              @RequestParam("visible") Integer visible) {
        buildAppMenuService.updateApplicationMenuVisibleMobile(id, visible);
        return CommonResult.success(true);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制应用菜单")
    public CommonResult<MenuCreateRespVO> copyApplicationMenu(@RequestBody MenuCopyReqVO copyReqVO) {
        return CommonResult.success(buildAppMenuService.copyApplicationMenu(copyReqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用菜单")
    public CommonResult<Boolean> deleteApplicationMenu(@RequestParam("id") Long id) {
        buildAppMenuService.deleteApplicationMenu(id);
        return CommonResult.success(true);
    }

}
