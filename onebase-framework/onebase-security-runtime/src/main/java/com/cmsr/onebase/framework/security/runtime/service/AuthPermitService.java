package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.framework.security.config.SecurityProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@Slf4j
@Service
public class AuthPermitService implements ApplicationContextAware {

    @Resource
    private SecurityProperties securityProperties;

    private ApplicationContext applicationContext;

    @Getter
    private List<String> permitAllUrls;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // 初始化时调用getPermitAllUrls方法并保存结果
        try {
            this.permitAllUrls = getPermitAllUrls();
        } catch (Exception e) {
            log.error("初始化免登录URL列表失败", e);
            this.permitAllUrls = new ArrayList<>();
        }
    }

    /**
     * 判断请求路径是否在免登录URL列表中
     * 支持Ant风格路径匹配，如：/runtime/system/auth/**
     *
     * @param request HTTP请求
     * @return 是否为免登录请求
     */
    public boolean isPermitAllRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (permitAllUrls != null) {
            for (String pattern : permitAllUrls) {
                if (pathMatcher.match(pattern, requestUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 解析和获取免登接口
     * 获取所有标注 @PermitAll 注解的Controller接口路径，以及 yaml 配置的免登录 URL 列表(securityProperties.getPermitAllUrls())
     *
     * @return
     */
    private List<String> getPermitAllUrls() {
        List<String> result = new ArrayList<>();

        // 添加配置文件中的免登录URL列表
        if (securityProperties.getPermitAllUrls() != null) {
            result.addAll(securityProperties.getPermitAllUrls());
        }

        // 获取带有@PermitAll注解的接口URL
        if (applicationContext != null) {
            RequestMappingHandlerMapping requestMappingHandlerMapping =
                    applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            if (requestMappingHandlerMapping != null) {
                Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
                    HandlerMethod handlerMethod = entry.getValue();
                    if (handlerMethod.hasMethodAnnotation(jakarta.annotation.security.PermitAll.class)) {
                        RequestMappingInfo requestMappingInfo = entry.getKey();

                        Set<String> urls = new HashSet<>();
                        if (requestMappingInfo.getPatternsCondition() != null) {
                            urls.addAll(requestMappingInfo.getPatternsCondition().getPatterns());
                        }
                        if (requestMappingInfo.getPathPatternsCondition() != null) {
                            urls.addAll(convertList(requestMappingInfo.getPathPatternsCondition().getPatterns(),
                                    pathPattern -> pathPattern.getPatternString()));
                        }

                        if (!urls.isEmpty()) {
                            result.addAll(urls);
                        }
                    }
                }
            }
        }

        return result;
    }
}
