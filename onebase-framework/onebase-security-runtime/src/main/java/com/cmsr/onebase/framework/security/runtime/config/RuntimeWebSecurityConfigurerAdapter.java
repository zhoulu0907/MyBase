package com.cmsr.onebase.framework.security.runtime.config;

import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.security.runtime.filter.RemoteCallAuthenticationFilter;
import com.cmsr.onebase.framework.security.runtime.filter.RuntimeApplicationContextHeaderFilter;
import com.cmsr.onebase.framework.security.runtime.filter.RuntimeAuthenticationFilter;
import com.cmsr.onebase.framework.web.config.WebProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 自定义的 Spring Security 配置适配器实现
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // 目的：先于 Spring Security 自动配置，避免一键改包后，org.* 基础包无法生效
@EnableMethodSecurity(securedEnabled = true)
public class RuntimeWebSecurityConfigurerAdapter {

    /**
     * 认证失败处理类 Bean
     */
    @Resource
    private AuthenticationEntryPoint    authenticationEntryPoint;
    /**
     * 权限不够处理器 Bean
     */
    @Resource
    private AccessDeniedHandler         accessDeniedHandler;
    /**
     * Token 认证过滤器 Bean
     */
    @Resource
    private RuntimeAuthenticationFilter runtimeAuthenticationTokenFilter;

    @Resource
    private RemoteCallAuthenticationFilter remoteCallAuthenticationFilter;

    @Resource
    private RuntimeApplicationContextHeaderFilter runtimeApplicationContextHeaderFilter;

    @Resource
    private WebProperties      webProperties;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 由于 Spring Security 创建 AuthenticationManager 对象时，没声明 @Bean 注解，导致无法被注入
     * 通过覆写父类的该方法，添加 @Bean 注解，解决该问题
     */
    @Bean
    public AuthenticationManager runtimeAuthenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置 URL 的安全配置
     */
    @Bean
    protected SecurityFilterChain runtimeFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 设置运行时API的安全匹配器
        RequestMatcher runtimeApiMatcher = new OrRequestMatcher(new AntPathRequestMatcher(webProperties.getRuntimeApi().getPrefix() + "/**"));
        httpSecurity.securityMatcher(runtimeApiMatcher);

        httpSecurity
                // 开启跨域
                .cors(Customizer.withDefaults())
                // CSRF 禁用，因为不使用 Session
                .csrf(AbstractHttpConfigurer::disable)
                // 基于 token 机制，所以不需要 Session
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 一堆自定义的 Spring Security 处理器
                .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));

        // 获得 @PermitAll 带来的 URL 列表，免登录
        // Multimap<HttpMethod, String> permitAllUrls = getPermitAllUrlsFromAnnotations();
        // 设置每个请求的权限
        // httpSecurity
        //         // ①：全局共享规则
        //         .authorizeHttpRequests(c -> c
        //                 // 1.1 静态资源，可匿名访问
        //                 .requestMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js", "/admin-api/app/application/get/**", "/runtime/app/application/get/**").permitAll()
        //                 // 1.2 设置 @PermitAll 无需认证
        //                 .requestMatchers(HttpMethod.GET, permitAllUrls.get(HttpMethod.GET).toArray(new String[0])).permitAll()
        //                 .requestMatchers(HttpMethod.POST, permitAllUrls.get(HttpMethod.POST).toArray(new String[0])).permitAll()
        //                 // 1.3 基于 onebase.security.permit-all-urls 无需认证
        //                 .requestMatchers(securityProperties.getPermitAllUrls().toArray(new String[0])).permitAll())
        //         // ③：兜底规则，必须认证
        //         .authorizeHttpRequests(c -> c.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll() // WebFlux 异步请求，无需认证，目的：SSE 场景
        //                 .anyRequest().authenticated());

        // 添加 Token Filter
        httpSecurity.addFilterBefore(remoteCallAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAfter(runtimeAuthenticationTokenFilter, RemoteCallAuthenticationFilter.class);
        httpSecurity.addFilterAfter(runtimeApplicationContextHeaderFilter, RuntimeAuthenticationFilter.class);
        return httpSecurity.build();
    }


    /**
     * 解析和获取免登接口
     * @return
     */
    // private Multimap<HttpMethod, String> getPermitAllUrlsFromAnnotations() {
    //     Multimap<HttpMethod, String> result = HashMultimap.create();
    //     // 获得接口对应的 HandlerMethod 集合
    //     RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
    //     Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
    //     // 获得有 @PermitAll 注解的接口
    //     for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
    //         HandlerMethod handlerMethod = entry.getValue();
    //         if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
    //             continue;
    //         }
    //         Set<String> urls = new HashSet<>();
    //         if (entry.getKey().getPatternsCondition() != null) {
    //             urls.addAll(entry.getKey().getPatternsCondition().getPatterns());
    //         }
    //         if (entry.getKey().getPathPatternsCondition() != null) {
    //             urls.addAll(convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
    //         }
    //         if (urls.isEmpty()) {
    //             continue;
    //         }
    //
    //         // 特殊：使用 @RequestMapping 注解，并且未写 method 属性，此时认为都需要免登录
    //         Set<RequestMethod> methods = entry.getKey().getMethodsCondition().getMethods();
    //         if (CollUtil.isEmpty(methods)) {
    //             result.putAll(HttpMethod.GET, urls);
    //             result.putAll(HttpMethod.POST, urls);
    //             continue;
    //         }
    //         // 根据请求方法，添加到 result 结果
    //         entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
    //             switch (requestMethod) {
    //                 case GET:
    //                     result.putAll(HttpMethod.GET, urls);
    //                     break;
    //                 case POST:
    //                     result.putAll(HttpMethod.POST, urls);
    //                     break;
    //             }
    //         });
    //     }
    //     return result;
    // }


}