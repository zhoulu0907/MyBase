package com.cmsr.onebase.module.system.runtime.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.ThirdSupplementUserReqVO;
import com.cmsr.onebase.module.system.vo.user.ThirdSupplementUserResVO;
import com.cmsr.onebase.module.system.vo.user.ThirdUserRegisterReqVO;
import com.cmsr.onebase.module.system.vo.user.UserForgetPasswordReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "外部用户服务 - 用户")
@RestController
@RequestMapping("/system/third-user")
@Validated
public class RuntimeThirdUserController {

    @Resource
    private UserService userService;


    @PostMapping("/register")
    @Operation(summary = "第三方注册用户")
    @PermitAll
    public CommonResult<Long> register(@RequestBody  @Valid ThirdUserRegisterReqVO reqVO) {
        Long userId = userService.thirdUserRegister(reqVO);
        return success(userId);
    }

    @PostMapping("/supplement-user")
    @Operation(summary = "补充用户信息")
    @PermitAll
    public CommonResult<ThirdSupplementUserResVO> supplementUser(@RequestBody  @Valid ThirdSupplementUserReqVO reqVO) {
        ThirdSupplementUserResVO user = userService.supplementUser(reqVO);
        return success(user);
    }


    @PostMapping("/forget-password")
    @Operation(summary = "忘记密码")
    @PermitAll
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserForgetPasswordReqVO reqVO) {
        userService.forgetPassword(reqVO);
        return success(true);
    }

}
