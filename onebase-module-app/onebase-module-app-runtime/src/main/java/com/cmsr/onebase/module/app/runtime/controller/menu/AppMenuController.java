package com.cmsr.onebase.module.app.runtime.controller.menu;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.runtime.service.menu.AppMenuService;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
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
    public CommonResult<List<MenuListRespVO>> listBpmApplicationMenu() {
        return success(appMenuService.listBpmApplicationMenu());
    }

    @GetMapping("/list")
    @Operation(summary = "应用菜单列表")
    public CommonResult<List<MenuListRespVO>> listApplicationMenu() {
        return success(appMenuService.listApplicationMenu());
    }

    @PostMapping("/permission")
    @Operation(summary = "应用菜单权限")
    public CommonResult<MenuPermissionVO> getMenuPermission(@RequestParam("menuId") Long menuId) {
        return success(appMenuService.getMenuPermission(menuId));
    }

}
