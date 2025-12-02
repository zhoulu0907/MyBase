package com.cmsr.onebase.module.system.build.controller.permission;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuListReqVO;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuRespVO;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuSaveVO;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuSimpleRespVO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 菜单&权限")
@RestController
@RequestMapping("/system/menu")
@Validated
public class SystemMenuController {

    @Resource
    private MenuService menuService;

    @PostMapping("/create")
    @Operation(summary = "创建权限")
    @PreAuthorize("@ss.hasPermission('tenant:menu:create')")
    public CommonResult<Long> createMenu(@Valid @RequestBody SystemMenuSaveVO createReqVO) {
        Long menuId = menuService.createMenu(createReqVO);
        return success(menuId);
    }

    @PostMapping("/update")
    @Operation(summary = "修改权限")
    @PreAuthorize("@ss.hasPermission('tenant:menu:update')")
    public CommonResult<Boolean> updateMenu(@Valid @RequestBody SystemMenuSaveVO updateReqVO) {
        menuService.updateMenu(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除权限")
    @Parameter(name = "id", description = "菜单编号", required= true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:menu:delete')")
    public CommonResult<Boolean> deleteMenu(@RequestParam("id") Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取菜单&权限列表", description = "用于【菜单管理】界面")
    @PreAuthorize("@ss.hasPermission('tenant:menu:query')")
    public CommonResult<List<SystemMenuRespVO>> getMenuList(SystemMenuListReqVO reqVO) {
        List<MenuDO> list = menuService.getAllActiveMenuList(reqVO);
        list.sort(Comparator.comparing(MenuDO::getSort));
        return success(BeanUtils.toBean(list, SystemMenuRespVO.class));
    }

    @GetMapping({ "simple-list"})
    @Operation(summary = "获取权限（开启状态）精简列表",
            description = "只包含被开启的菜单，用于【角色分配菜单】功能的选项。在多租户的场景下，会只返回租户所在套餐有的菜单")
    public CommonResult<List<SystemMenuSimpleRespVO>> getSimpleMenuList(SystemMenuListReqVO reqVO) {
        List<MenuDO> list = menuService.getMenuListByTenant(reqVO);
        list = menuService.filterDisableMenus(list);
        list.sort(Comparator.comparing(MenuDO::getSort));
        return success(BeanUtils.toBean(list, SystemMenuSimpleRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取菜单&权限信息")
    @PreAuthorize("@ss.hasPermission('tenant:menu:query')")
    public CommonResult<SystemMenuRespVO> getMenu(Long id) {
        MenuDO menu = menuService.getMenu(id);
        return success(BeanUtils.toBean(menu, SystemMenuRespVO.class));
    }

}
