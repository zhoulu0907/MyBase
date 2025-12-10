package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.excel.core.util.ExcelUtils;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.common.SexEnum;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.dept.DeptSimpleListRespVO;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/system/user")
@Validated
public class TenantUserController {

    @Resource
    private UserService userService;

    @Resource
    private DeptService deptService;

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserInsertReqVO reqVO) {
        Long id = userService.createUser(reqVO);
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @PostMapping("/update-password")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('tenant:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('tenant:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = userService.getUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        List<UserRespVO> userRespVOList =userService.getConvertUserPage(pageResult);
        return success(new PageResult<>(userRespVOList, pageResult.getTotal()));
    }

    @GetMapping("/simple-page")
    @Operation(summary = "获得简要用户分页列表(启用状态)", description = "只包含开启的用户，主要用于前端的下拉选项")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<PageResult<UserSimpleRespVO>> getUserSimplePage(@Valid UserSimplePageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = userService.getSimpleEnableUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal()));
    }


    @GetMapping("/simple-list")
    @Operation(summary = "获取用户精简信息列表", description = "只包含开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserDeptSimpleRespVO>> getSimpleUserList() {
        List<AdminUserDO> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus(), null);
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }


    @GetMapping("/simple-list-by-dept-id")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    @Operation(summary = "通过部门id获取用户精简信息列表", description = "只包含开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserDeptSimpleRespVO>> getSimpleUserListByDeptId (@Valid DeptSimpleListRespVO respVO) {
        List<AdminUserDO> list = userService.getUserListByStatusAndDeptId(respVO);
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }


    @GetMapping("/simple-list-by-name")
    @Operation(summary = "通过昵称获取用户精简信息列表", description = "只包含开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserDeptSimpleRespVO>> getSimpleUserListByName(@RequestParam("userNickName") String userNickName) {
        List<AdminUserDO> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus(), userNickName);
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        UserRespVO userDetail = userService.getUserWithRoles(id);
        return success(userDetail);
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:export')")
    public void exportUserList(@Validated UserPageReqVO exportReqVO, HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdminUserDO> list = userService.getUserPage(exportReqVO).getList();
        // 输出 Excel
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        ExcelUtils.write(response, "用户数据.xls", "数据", UserRespVO.class, UserConvert.INSTANCE.convertList(list, deptMap));
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入用户模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelVO> list = Arrays.asList(UserImportExcelVO.builder().username("yunai").deptId(1L).email("yunai@aaa.com").mobile("15601691300").nickname("OneBase").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build(), UserImportExcelVO.builder().username("yuanma").deptId(2L).email("yuanma@aaa.com").mobile("15601701300").nickname("源码").status(CommonStatusEnum.DISABLE.getStatus()).sex(SexEnum.FEMALE.getSex()).build());
        // 输出
        ExcelUtils.write(response, "用户导入模板.xls", "用户列表", UserImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入用户")
    @Parameters({@Parameter(name = "file", description = "Excel 文件", required = true), @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")})
    @PreAuthorize("@ss.hasPermission('tenant:user:import')")
    public CommonResult<UserImportRespVO> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<UserImportExcelVO> list = ExcelUtils.read(file, UserImportExcelVO.class);
        return success(userService.importUserList(list, updateSupport));
    }

    @GetMapping("/get-user-page-by-dept")
    @Operation(summary = "获得指定部门的用户简要分页列表", description = "获取指定部门的直属用户简要信息（分页），isRecurseSub为true时包含所有下级部门用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<PageResult<UserSimpleRespVO>> getUserPageByDept(@Valid UserByDeptPageReqVO pageReqVO) {
        PageResult<AdminUserDO> pageResult = userService.getUserByDeptPage(pageReqVO);
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal()));
    }

}
