package com.cmsr.onebase.plugin.runtime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 插件安全配置
 * <p>
 * 配置插件相关的安全策略，例如静态资源放行。
 * </p>
 *
 * @author onebase
 * @date 2026-01-22
 */
@Configuration
public class PluginSecurityConfiguration {

    @Bean
    public WebSecurityCustomizer pluginWebSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/plugins/static/**"));
    }
}
