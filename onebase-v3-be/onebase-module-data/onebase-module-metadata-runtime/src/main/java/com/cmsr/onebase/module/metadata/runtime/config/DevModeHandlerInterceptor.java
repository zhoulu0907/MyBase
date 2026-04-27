package com.cmsr.onebase.module.metadata.runtime.config;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 开发模式拦截器
 *
 * <p>当请求参数 isDev=true 时，将 ThreadLocal 中的 versionTag 切换为编辑态（BUILD=0），
 * 使得查询元数据系统表时使用 version_tag=0 而非默认的 version_tag=1（运行态）。
 * 请求结束后自动恢复原值，不影响后续逻辑。</p>
 *
 * @author bty418
 * @date 2026-02-12
 */
public class DevModeHandlerInterceptor implements HandlerInterceptor {

    private static final String ATTR_ORIGINAL_VERSION_TAG = "DevMode_OriginalVersionTag";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String isDev = request.getParameter("isDev");
        if ("true".equalsIgnoreCase(isDev)) {
            Long originalVersionTag = ApplicationManager.getVersionTag();
            request.setAttribute(ATTR_ORIGINAL_VERSION_TAG, originalVersionTag);
            ApplicationManager.setVersionTag(VersionTagEnum.BUILD.getValue());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Object original = request.getAttribute(ATTR_ORIGINAL_VERSION_TAG);
        if (original != null) {
            ApplicationManager.setVersionTag((Long) original);
        }
    }
}
