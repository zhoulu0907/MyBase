package com.cmsr.onebase.framework.web.core.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.RpcConstants;
import com.cmsr.onebase.framework.common.enums.TerminalEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
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

    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID   = "login_user_id";
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type";

    @Deprecated
    public static final String HEADER_TENANT_ID       = "tenant-id";
    public static final String HEADER_VISIT_TENANT_ID = "X-Visit-Tenant-Id";


    public static final String HEADER_X_TENANT_ID = "X-Tenant-Id";
    public static final String HEADER_X_CORP_ID   = "X-Corp-Id";
    public static final String HEADER_X_APP_ID    = "X-App-Id";
    public static final String X_From_Scene_Type  = "X-From-Scene-Type";

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

    /**
     * 获得请求对应的用户类型
     *
     * @param request 请求
     * @return 用户类型
     */
    public static Integer getLoginUserType(HttpServletRequest request) {
        // 获得请求的 URI
        String uri = request.getRequestURI();

        // 检查 Admin API
        if (StrUtil.startWith(uri, properties.getBuildApi().getPrefix())) {
            return UserTypeEnum.THIRD.getValue();
        }
        // 检查 App API
        if (StrUtil.startWith(uri, properties.getRuntimeApi().getPrefix())) {
            return UserTypeEnum.CORP.getValue();
        }
        // 检查 App API
        if (StrUtil.startWith(uri, properties.getPlatformApi().getPrefix())) {
            return UserTypeEnum.PLATFORM.getValue();
        }
        return null;
    }

    public static Integer getLoginUserType() {
        HttpServletRequest request = getRequest();
        return getLoginUserType(request);
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
    public static Long getCorpIdFromHeader() {
        HttpServletRequest request = getRequest();
        String corpID = request.getHeader(HEADER_X_CORP_ID);
        return NumberUtil.isNumber(corpID) ? Long.valueOf(corpID) : null;
    }

    @Deprecated
    public static String getXFromSceneTypeFromHeader() {
        HttpServletRequest request = getRequest();
        String fromType = request.getHeader(X_From_Scene_Type);
        // TODO 后期更新前端后，取消默认平台, 不允许空，并校验和用户实际类型（user_type）一致
        if (StringUtils.isBlank(fromType)) {
            String uri = request.getRequestURI();
            // 检查 Admin API
            if (StrUtil.startWith(uri, properties.getPlatformApi().getPrefix())) {
                fromType = XFromSceneTypeEnum.PLATFORM.getCode();
            }
        }
        return fromType == null ? XFromSceneTypeEnum.TENANT.getCode() : fromType;
    }

}
