package com.cmsr.onebase.framework.web.core.util;

import cn.hutool.core.util.NumberUtil;
import com.cmsr.onebase.framework.common.enums.RpcConstants;
import com.cmsr.onebase.framework.common.enums.TerminalEnum;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.config.WebProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 专属于 web 包的工具类
 */
public class WebFrameworkUtils {

    @Deprecated
    public static final String HEADER_TENANT_ID       = "tenant-id";
    public static final String HEADER_VISIT_TENANT_ID = "X-Visit-Tenant-Id";


    public static final String HEADER_X_TENANT_ID = "X-Tenant-Id";
    // public static final String HEADER_X_CORP_ID   = "X-Corp-Id";
    // public static final String HEADER_X_APP_ID    = "X-App-Id";

    /**
     * 终端的 Header
     *
     * @see com.cmsr.onebase.framework.common.enums.TerminalEnum
     */
    public static final String HEADER_TERMINAL = "terminal";

    private static WebProperties properties;

    public WebFrameworkUtils(WebProperties webProperties) {
        WebFrameworkUtils.properties = webProperties;
    }

    /**
     * 获得租户编号，从 header 中
     * 考虑到其它 framework 组件也会使用到租户编号，所以不得不放在 WebFrameworkUtils 统一提供
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getTenantIdFromHeader(HttpServletRequest request) {
        // 启用 X-Tenant-Id 读取租户ID
        String tenantId = request.getHeader(HEADER_X_TENANT_ID);
        // 读取 HEADER_TENANT_ID（老租户ID字段） 后续删除
        if (StringUtils.isBlank(tenantId)) {
            tenantId = request.getHeader(HEADER_TENANT_ID);
        }
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 获得访问的租户编号，从 header 中
     * 考虑到其它 framework 组件也会使用到租户编号，所以不得不放在 WebFrameworkUtils 统一提供
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getVisitTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_VISIT_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    public static Long getLoginUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    public static Integer getTerminal() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return TerminalEnum.UNKNOWN.getTerminal();
        }
        String terminalValue = request.getHeader(HEADER_TERMINAL);
        return NumberUtil.parseInt(terminalValue, TerminalEnum.UNKNOWN.getTerminal());
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 判断是否为 RPC 请求
     *
     * @param request 请求
     * @return 是否为 RPC 请求
     */
    public static boolean isRpcRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(RpcConstants.RPC_API_PREFIX);
    }

    /**
     * 判断是否为 RPC 请求
     * <p>
     * 约定大于配置，只要以 Api 结尾，都认为是 RPC 接口
     *
     * @param className 类名
     * @return 是否为 RPC 请求
     */
    public static boolean isRpcRequest(String className) {
        return className.endsWith("Api");
    }

    /**
     * 获取企业id
     *
     * @return
     */
    // public static Long getCorpIdFromHeader() {
    //     HttpServletRequest request = getRequest();
    //     String corpID = request.getHeader(HEADER_X_CORP_ID);
    //     return NumberUtil.isNumber(corpID) ? Long.valueOf(corpID) : null;
    // }
}
