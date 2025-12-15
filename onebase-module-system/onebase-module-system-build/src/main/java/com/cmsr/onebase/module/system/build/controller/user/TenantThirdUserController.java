package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpRelationAppReqVO;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "空间外部用户服务 - 用户")
@RestController
@RequestMapping("/system/third")
@Validated
public class TenantThirdUserController {

    @Resource
    private UserService userService;

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:create')")
    public CommonResult<Long> createUserAndUserAppRelation(@Valid @RequestBody ThirdUserAppCombinedInsertReqVO reqVO) {
        Long id = userService.createUserAndUserAppRelation(reqVO);
        return success(id);
    }


    @PostMapping("/update")
    @Operation(summary = "编辑用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:update')")
    public CommonResult<Long> updateUserAndUserAppRelation(@RequestBody @Valid ThirdUserAppCombinedUpdateReqVO reqVO) {
        Long id = userService.updateUserAndUserAppRelation(reqVO);
        return success(id);
    }

    @PostMapping("/update-password")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('tenant:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody ThirdUserUpdatePasswordReqVO reqVO) {
        userService.updateThirdUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('tenant:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
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

    @GetMapping("/user-applications-page")
    @Operation(summary = "获得三方用户(包含授权列表)-分页")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<PageResult<UserApplicationRespVO>> getUserAppRelationPage(@Valid UserAppPageSearchReqVO userAppPageReqVO) {
        PageResult<UserApplicationRespVO> pageResult = userService.getUserAppRelationPage(userAppPageReqVO);
        return success(pageResult);
    }


}
