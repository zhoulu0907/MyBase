package com.cmsr.onebase.module.system.framework.security.config;

import cn.hutool.crypto.asymmetric.SM2;
import com.cmsr.onebase.framework.common.consts.ENConstant;
import com.cmsr.onebase.framework.security.config.AuthorizeRequestsCustomizer;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * System 模块的 Security 配置
 */
@Configuration(proxyBeanMethods = false, value = "systemSecurityConfiguration")
public class SecurityConfiguration {

    @Bean("pwdSM2")
    public SM2 sm2() {
        // 使用指定私钥初始化 SM2
        String privateKey = ENConstant.EN_P_SM2KEY;
        String publicKey = ENConstant.EN_P_SM2KEY_PUBLIC;
        SM2 sm2 = new SM2(privateKey, publicKey);
        // 使用 C1C3C2 模式解密,与前端保持一致
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        return sm2;
    }

    @Bean("systemAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // TODO 开发者：这个每个项目都需要重复配置，得捉摸有没通用的方案
                // Swagger 接口文档
                registry.requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll();
                // Spring Boot Actuator 的安全配置
                registry.requestMatchers("/actuator").permitAll()
                        .requestMatchers("/actuator/**").permitAll();
                // RPC 服务的安全配置
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
            }

        };
    }

}
