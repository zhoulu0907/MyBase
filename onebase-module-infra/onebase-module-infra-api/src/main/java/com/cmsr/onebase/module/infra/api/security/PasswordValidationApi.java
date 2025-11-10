package com.cmsr.onebase.module.infra.api.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 密码校验API接口
 * 
 * 提供跨模块的密码校验能力
 * 其他模块可通过Feign调用此接口，对密码进行强度检查
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@RequestMapping("/infra/api/password-validation")
@Tag(name = "基础设施 - 密码校验")
@FeignClient(name = "infra-service")
public interface PasswordValidationApi {

    /**
     * 校验密码强度
     * 
     * 基于当前租户的密码策略配置，对密码进行强度检查
     * 如果密码不符合要求，返回相应的错误信息
     *
     * @param password 待校验的密码
     * @return 校验结果，成功返回success，失败返回error及错误信息
     */
    @PostMapping("/validate")
    CommonResult<Boolean> validatePassword(@RequestParam("password") String password);

}
