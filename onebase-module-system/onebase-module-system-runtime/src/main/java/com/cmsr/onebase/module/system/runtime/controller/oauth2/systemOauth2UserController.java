package com.cmsr.onebase.module.system.runtime.controller.oauth2;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2UserService;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运行态第三方获取token信息相关服务
 *
 * @author yuxin
 * @date 2026-02
 */
@Tag(name = "Runtime - 第三方应用获取token信息")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
public class systemOauth2UserController {

    @Resource
    private OAuth2UserService oauth2UserService;

    @PostMapping("/get")
    @Operation(summary = "获取用户信息")
    @PermitAll
    @ApiSignIgnore
    CommonResult<UserSimpleRespVO> getUser(@RequestParam("access_token") String accessToken) {

        return CommonResult.success(oauth2UserService.getUserInfoByToken(accessToken));
    };

}