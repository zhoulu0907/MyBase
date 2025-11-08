package com.cmsr.onebase.module.infra.build.apiimpl;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.security.PasswordValidationApi;
import com.cmsr.onebase.module.infra.service.security.PasswordValidationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

    /**
     * 密码校验API实现类
     *
     * 提供密码强度校验的REST端点，供其他模块通过Feign调用
     *
     * @author chengyuansen
     * @date 2025-11-07
     */
    @Slf4j
    @RestController
    public class PasswordValidationApiImpl implements PasswordValidationApi {

        @Resource
        private PasswordValidationService passwordValidationService;

        @Override
        @Operation(summary = "校验密码强度")
        public CommonResult<Void> validatePassword(@RequestParam("password") String password) {
            try {
                passwordValidationService.validatePassword(password);
                return success(null);
            } catch (ServiceException e) {
                log.warn("密码强度校验失败: {}", e.getMessage());
                // 将ServiceException直接抛出，由全局异常处理器处理
                throw e;
            } catch (Exception e) {
                log.error("密码校验过程中发生异常", e);
                throw e;
            }
        }
    }
