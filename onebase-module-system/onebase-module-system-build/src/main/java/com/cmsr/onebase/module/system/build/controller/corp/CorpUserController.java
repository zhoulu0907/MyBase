package com.cmsr.onebase.module.system.build.controller.corp;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.excel.core.util.ExcelUtils;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "企业服务 - 用户管理")
@RestController
@RequestMapping("/corp/user")
@Validated
public class CorpUserController {

    @Resource
    private UserService corpUserService;
    @Resource
    private DeptService corpDeptService;

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('corp:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserInsertReqVO reqVO) {
        Long id = corpUserService.createUser(reqVO);
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermission('corp:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        corpUserService.updateUser(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('corp:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        corpUserService.deleteUser(id);
        return success(true);
    }

    @PostMapping("/update-password")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('corp:user:reset-pwd')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        corpUserService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('corp:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        corpUserService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = corpUserService.getUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        // 拼接数据
        Map<Long, DeptDO> deptMap = corpDeptService.getDeptMap(
                convertList(pageResult.getList(), AdminUserDO::getDeptId));
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList(), deptMap),
                pageResult.getTotal()));
    }

    @GetMapping("/simple-page")
    @Operation(summary = "获得简要用户分页列表(启用状态)", description = "只包含开启的用户，主要用于前端的下拉选项")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    public CommonResult<PageResult<UserSimpleRespVO>> getUserSimplePage(@Valid UserSimplePageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = corpUserService.getSimpleEnableUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal()));
    }


    @GetMapping("/simple-list")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    @Operation(summary = "获取用户精简信息列表", description = "只包含开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserDeptSimpleRespVO>> getSimpleUserList() {
        List<AdminUserDO> list = corpUserService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus(),null);
        // 拼接数据
        Map<Long, DeptDO> deptMap = corpDeptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/simple-list-by-name")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    @Operation(summary = "通过昵称获取用户精简信息列表", description = "只包含开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserDeptSimpleRespVO>> getSimpleUserListByName(@RequestParam("userNickName") String userNickName) {
        List<AdminUserDO> list = corpUserService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus(), userNickName);
        // 拼接数据
        Map<Long, DeptDO> deptMap = corpDeptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        UserRespVO userDetail = corpUserService.getUserWithRoles(id);
        return success(userDetail);
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermission('corp:user:export')")
    public void exportUserList(@Validated UserPageReqVO exportReqVO,
                               HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdminUserDO> list = corpUserService.getUserPage(exportReqVO).getList();
        // 输出 Excel
        Map<Long, DeptDO> deptMap = corpDeptService.getDeptMap(
                convertList(list, AdminUserDO::getDeptId));
        ExcelUtils.write(response, "用户数据.xls", "数据", UserRespVO.class,
                UserConvert.INSTANCE.convertList(list, deptMap));
    }

    @GetMapping("/get-user-page-by-dept")
    @Operation(summary = "获得指定部门的用户简要分页列表", description = "获取指定部门的直属用户简要信息（分页），isRecurseSub为true时包含所有下级部门用户")
    @PreAuthorize("@ss.hasPermission('corp:user:query')")
    public CommonResult<PageResult<UserSimpleRespVO>> getUserPageByDept(
            @Valid UserByDeptPageReqVO pageReqVO) {
        PageResult<AdminUserDO> pageResult = corpUserService.getUserByDeptPage(pageReqVO);
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal()));
    }

}
