package com.cmsr.onebase.module.system.service.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.convert.auth.AuthConvert;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.enums.logger.LoginResultEnum;
import com.cmsr.onebase.module.system.enums.oauth2.OAuth2ClientConstants;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.service.logger.LoginLogService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantPackageService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.util.oauth2.LingjiSsoSignatureUtils;
import com.cmsr.onebase.module.system.util.oauth2.OkHttpClientUtils;
import com.cmsr.onebase.module.system.vo.auth.AuthLoginRespVO;
import com.cmsr.onebase.module.system.vo.auth.LingjiSsoUserInfoVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantAdminUserReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantUpdateReqVO;
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserUpdateReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 灵畿平台 SSO 登录 Service 实现类
 */
@Service
@Slf4j
public class LingjiSsoServiceImpl implements LingjiSsoService {

    @Resource
    private LingjiSsoProperties lingjiSsoProperties;

    @Resource
    private TenantService tenantService;

    @Resource
    private TenantPackageService tenantPackageService;

    @Resource
    private UserService userService;

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private RoleService roleService;

    @Resource
    private SecurityConfigApi securityConfigApi;

    /**
     * 默认租户套餐编码
     */
    private static final String DEFAULT_PACKAGE_CODE = "default";

    /**
     * SSO 首次登录自动建租户时，兜底的操作人 ID。
     */
    private static final Long SSO_FALLBACK_OPERATOR_USER_ID = 1L;

    @Override
    public AuthLoginRespVO login(String code, String deviceId) {
        // 1. 校验配置是否启用
        if (!lingjiSsoProperties.isEnabled()) {
            throw exception(LINGJI_SSO_CONFIG_DISABLED);
        }

        // 2. 获取用户信息
        LingjiSsoUserInfoVO userInfo = getUserInfo(code);

        // 3. 查找租户
        TenantDO tenantDo = findTenant(userInfo.getEnterpriseId());

        // 4. 如果租户不存在，创建新租户
        if (tenantDo == null) {
            tenantDo = createTenant(userInfo);
        }

        tenantService.validTenant(tenantDo.getId());

        // 5. 处理用户信息并登录
        return processUserLogin(userInfo, deviceId, tenantDo);
    }

    /**
     * 获取灵畿用户信息
     */
    private LingjiSsoUserInfoVO getUserInfo(String code) {
        // 校验必需的配置项
        if (StringUtils.isBlank(lingjiSsoProperties.getSourceId())) {
            log.error("灵畿 SSO 配置错误：sourceId 不能为空");
            throw exception(LINGJI_SSO_CONFIG_DISABLED);
        }
        if (StringUtils.isBlank(lingjiSsoProperties.getSourceKey())) {
            log.error("灵畿 SSO 配置错误：sourceKey 不能为空");
            throw exception(LINGJI_SSO_CONFIG_DISABLED);
        }
        if (StringUtils.isBlank(lingjiSsoProperties.getUserInfoUrl())) {
            log.error("灵畿 SSO 配置错误：userInfoUrl 不能为空");
            throw exception(LINGJI_SSO_CONFIG_DISABLED);
        }
    
        // 生成时间戳 (格式：yyyyMMddHHmmssSSS)
        String timestamp = LingjiSsoSignatureUtils.generateTimestamp();

        // 生成 requestId
        String requestId = LingjiSsoSignatureUtils.generateRequestId();

        // 构建 head 参数（用于签名）
        Map<String, String> headParams = new HashMap<>();
        headParams.put("requestId", requestId);
        headParams.put("timestamp", timestamp);
        headParams.put("version", "1.0");
        headParams.put("sourceid", lingjiSsoProperties.getSourceId());

        // 构建 data 参数（用于签名）
        Map<String, String> dataParams = new HashMap<>();
        dataParams.put("code", code);

        // 生成签名
        String signature;
        try {
            signature = LingjiSsoSignatureUtils.generateSignature(
                headParams, dataParams, lingjiSsoProperties.getSourceKey(), timestamp);
        } catch (Exception e) {
            log.error("灵畿SSO签名失败", e);
            throw exception(LINGJI_SSO_SIGNATURE_ERROR);
        }

        // 构建请求体
        JSONObject head = new JSONObject();
        head.set("requestId", requestId);
        head.set("timestamp", timestamp);
        head.set("version", "1.0");
        head.set("sourceid", lingjiSsoProperties.getSourceId());
        head.set("sign", signature);

        JSONObject data = new JSONObject();
        data.set("code", code);

        JSONObject requestBody = new JSONObject();
        requestBody.set("head", head);
        requestBody.set("data", data);

        log.info("灵畿SSO获取用户信息请求: {}", requestBody.toString());

        // 创建请求
        Request request = new Request.Builder()
            .url(lingjiSsoProperties.getUserInfoUrl())
            .post(okhttp3.RequestBody.create(
                requestBody.toString(),
                okhttp3.MediaType.parse("application/json; charset=utf-8")))
            .build();

        // 发送请求
        String responseBody = OkHttpClientUtils.sendRequest(request, lingjiSsoProperties.isHttpDebugLogEnabled());
        log.info("灵畿SSO获取用户信息响应: {}", responseBody);

        // 解析响应
        JSONObject responseObj = JSONUtil.parseObj(responseBody);

        // 检查响应状态（响应格式：{"head": {"respStatus": "00", "respCode": "00", "respDesc": "..."}, "data": {...}}）
        JSONObject respHead = responseObj.getJSONObject("head");
        if (respHead != null) {
            String respStatus = respHead.getStr("respStatus");
            String respCode = respHead.getStr("respCode");
            String respDesc = respHead.getStr("respDesc");

            // 成功状态为 "00"，或者 respStatus/respCode 都为 null 也算成功
            boolean isSuccess = (respStatus == null && respCode == null)
                    || ("00".equals(respStatus) && "00".equals(respCode));

            if (!isSuccess) {
                log.error("灵畿SSO获取用户信息失败: respStatus={}, respCode={}, respDesc={}", respStatus, respCode, respDesc);
                // 授权码无效或已过期
                if (respDesc != null && respDesc.contains("授权码无效")) {
                    throw exception(LINGJI_SSO_CODE_INVALID);
                }
                throw exception(LINGJI_SSO_GET_TOKEN_FAILED);
            }
        } else {
            // 兼容旧格式：直接在根级别有 status 字段
            String status = responseObj.getStr("status");
            if (!"0".equals(status)) {
                log.error("灵畿SSO获取用户信息失败: status={}, message={}", status, responseObj.getStr("message"));
                throw exception(LINGJI_SSO_GET_TOKEN_FAILED);
            }
        }

        // 获取 idToken（可能在 data 对象中，也可能在根级别；可能是 idToken 或 id_token）
        String idToken = null;
        JSONObject respData = responseObj.getJSONObject("data");
        if (respData != null) {
            idToken = respData.getStr("idToken");
            if (StringUtils.isBlank(idToken)) {
                idToken = respData.getStr("id_token");
            }
        }
        if (StringUtils.isBlank(idToken)) {
            idToken = responseObj.getStr("idToken");
        }
        if (StringUtils.isBlank(idToken)) {
            idToken = responseObj.getStr("id_token");
        }
        if (StringUtils.isBlank(idToken)) {
            throw exception(LINGJI_SSO_USER_INFO_EMPTY);
        }

        // 解析 JWT
        return parseJwtPayload(idToken);
    }

    /**
     * 解析 JWT payload
     */
    private LingjiSsoUserInfoVO parseJwtPayload(String jwt) {
        try {
            // JWT 格式: header.payload.signature
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("无效的JWT格式");
            }

            // 解码 payload (第二部分)
            String payload = Base64.decodeStr(parts[1]);

            // 解析为对象
            return JSONUtil.toBean(payload, LingjiSsoUserInfoVO.class);
        } catch (Exception e) {
            log.error("解析JWT失败: {}", e.getMessage());
            throw exception(LINGJI_SSO_USER_INFO_EMPTY);
        }
    }

    /**
     * 查找租户（通过 enterpriseId 从数据库查询）
     */
    private TenantDO findTenant(String enterpriseId) {
        return tenantService.getTenantByCode(enterpriseId);
    }

    /**
     * 创建租户
     */
    private TenantDO createTenant(LingjiSsoUserInfoVO userInfo) {
        String enterpriseId = userInfo.getEnterpriseId();
        log.info("未找到企业租户映射，创建新租户: enterpriseId={}, userName={}", enterpriseId, userInfo.getUserName());

        // 获取默认套餐
        TenantPackageDO tenantPackage = tenantPackageService.getTenantPackageByCode(DEFAULT_PACKAGE_CODE);
        if (tenantPackage == null) {
            // 尝试获取第一个可用套餐
            List<TenantPackageDO> packages = tenantPackageService.getTenantPackageListByStatus(CommonStatusEnum.ENABLE.getStatus());
            if (packages.isEmpty()) {
                log.error("没有可用的租户套餐");
                throw exception(LINGJI_SSO_TENANT_NOT_FOUND);
            }
            tenantPackage = packages.get(0);
        }

        // 构建租户创建请求
        TenantInsertReqVO tenantReqVO = new TenantInsertReqVO();
        tenantReqVO.setName(buildTenantName(enterpriseId));
        tenantReqVO.setTenantCode(buildTenantCode(enterpriseId));
        tenantReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        tenantReqVO.setPackageId(tenantPackage.getId());
        tenantReqVO.setExpireTime(LocalDateTime.now().plusYears(10)); // 默认10年
        tenantReqVO.setAccountCount(10000); // 默认10000个账号
        // website 先设置基础地址，创建租户后更新
        tenantReqVO.setWebsite(lingjiSsoProperties.getTenantWebsite());

        // 设置管理员信息（当前登录用户）
        TenantAdminUserReqVO adminReqVO = new TenantAdminUserReqVO();
        adminReqVO.setAdminUserName(userInfo.getSub());  // 使用手机号作为账号
        adminReqVO.setAdminNickName(StringUtils.isNotBlank(userInfo.getUserName()) ? userInfo.getUserName() : adminReqVO.getAdminUserName());
        adminReqVO.setAdminMobile(userInfo.getSub());
        adminReqVO.setAdminEmail(userInfo.getEmail());
        tenantReqVO.setTenantAdminUserReqVOList(Collections.singletonList(adminReqVO));

        // 创建租户
        Long tenantId = createTenantWithFallbackOperator(tenantReqVO);
        log.info("创建租户成功: tenantId={}, enterpriseId={}, tenantCode={}", tenantId, enterpriseId, tenantReqVO.getTenantCode());

        // 更新租户 website，格式：/tenant/{tenantId}/{tenantCode}/
        String tenantWebsite = lingjiSsoProperties.getTenantWebsite() + "/tenant/" + tenantId + "/";
        updateTenantWebsite(tenantId, tenantWebsite);
        log.info("更新租户website: tenantId={}, website={}", tenantId, tenantWebsite);

        return tenantService.getTenant(tenantId);
    }

    /**
     * 在无登录态时，使用兜底操作人补充安全上下文，避免审计字段（creator/updater）为空导致插入失败。
     */
    private Long createTenantWithFallbackOperator(TenantInsertReqVO tenantReqVO) {
        if (SecurityFrameworkUtils.getLoginUserId() != null) {
            return tenantService.createTenant(tenantReqVO);
        }

        Authentication previousAuthentication = SecurityFrameworkUtils.getAuthentication();
        try {
            LoginUser fallbackLoginUser = new LoginUser();
            fallbackLoginUser.setId(SSO_FALLBACK_OPERATOR_USER_ID);
            fallbackLoginUser.setUserType(UserTypeEnum.PLATFORM.getValue());

            UsernamePasswordAuthenticationToken fallbackAuthentication =
                    new UsernamePasswordAuthenticationToken(fallbackLoginUser, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(fallbackAuthentication);

            return tenantService.createTenant(tenantReqVO);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
        }
    }

    /**
     * 构建租户名称
     */
    private String buildTenantName(String enterpriseId) {
        return "企业" + enterpriseId + "的空间";
    }

    /**
     * 构建租户编码
     */
    private String buildTenantCode(String enterpriseId) {
        return enterpriseId;
    }

    /**
     * 更新租户 website
     */
    private void updateTenantWebsite(Long tenantId, String website) {
        TenantUpdateReqVO updateReqVO = new TenantUpdateReqVO();
        updateReqVO.setId(tenantId);
        updateReqVO.setWebsite(website);
        tenantService.updateTenant(updateReqVO);
    }

    /**
     * 处理用户登录
     */
    private AuthLoginRespVO processUserLogin(LingjiSsoUserInfoVO userInfo, String deviceId, TenantDO tenantDo) {
        String loginDeviceId = normalizeDeviceId(deviceId);

        // 构建用户对象
        AdminUserDO adminUserDO = new AdminUserDO();
        adminUserDO.setMobile(userInfo.getSub()); // 手机号在 sub 字段
        adminUserDO.setNickname(userInfo.getUserName());
        adminUserDO.setUsername(userInfo.getSub());  // 使用手机号作为账号
        adminUserDO.setEmail(userInfo.getEmail());
        adminUserDO.setUserType(UserTypeEnum.TENANT.getValue());

        if (StringUtils.isBlank(adminUserDO.getNickname())) {
            adminUserDO.setNickname(adminUserDO.getUsername());
        }

        // 保存用户信息并创建 Token
        AtomicReference<AuthLoginRespVO> loginRespRef = new AtomicReference<>();
        TenantUtils.execute(tenantDo.getId(), () -> {
            // 查找或创建用户
            AdminUserDO adminUser = findOrCreateUser(adminUserDO, userInfo);
            // 创建 Token
            AuthLoginRespVO authLoginRespVO = createTokenAfterLoginSuccess(adminUser, loginDeviceId, tenantDo);
            loginRespRef.set(authLoginRespVO);
        });

        AuthLoginRespVO authLoginRespVO = loginRespRef.get();
        if (authLoginRespVO == null) {
            throw exception(LINGJI_SSO_CREATE_TOKEN_FAILED);
        }
        return authLoginRespVO;
    }

    /**
     * 查找或创建用户（只有关键信息变化时才更新）
     */
    private AdminUserDO findOrCreateUser(AdminUserDO userInfo, LingjiSsoUserInfoVO ssoUserInfo) {
        AdminUserDO existingUser = findExistingUser(userInfo);
        if (existingUser == null) {
            // 用户不存在，创建新用户
            log.info("用户不存在，创建新用户: username={}", userInfo.getUsername());
            try {
                return createUser(userInfo);
            } catch (ServiceException ex) {
                if (!isUserConflictException(ex)) {
                    throw ex;
                }
                log.warn("灵畿SSO创建用户发生并发冲突，改为复用已有用户: username={}, mobile={}, code={}",
                        userInfo.getUsername(), userInfo.getMobile(), ex.getCode());
                existingUser = findExistingUser(userInfo);
                if (existingUser == null) {
                    throw ex;
                }
            }
        }

        if (CommonStatusEnum.isDisable(existingUser.getStatus())) {
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }

        // 用户存在，检查关键信息是否变化
        boolean needUpdate = false;

        if (!Objects.equals(existingUser.getUsername(), userInfo.getUsername())) {
            log.info("用户名变化: {} -> {}", existingUser.getUsername(), userInfo.getUsername());
            existingUser.setUsername(userInfo.getUsername());
            needUpdate = true;
        }

        if (!Objects.equals(existingUser.getNickname(), userInfo.getNickname())) {
            log.info("昵称变化: {} -> {}", existingUser.getNickname(), userInfo.getNickname());
            existingUser.setNickname(userInfo.getNickname());
            needUpdate = true;
        }

        if (!Objects.equals(existingUser.getEmail(), userInfo.getEmail())) {
            log.info("邮箱变化: {} -> {}", existingUser.getEmail(), userInfo.getEmail());
            existingUser.setEmail(userInfo.getEmail());
            needUpdate = true;
        }

        if (!Objects.equals(existingUser.getMobile(), userInfo.getMobile())) {
            log.info("手机号变化: {} -> {}", existingUser.getMobile(), userInfo.getMobile());
            existingUser.setMobile(userInfo.getMobile());
            needUpdate = true;
        }

        // 只有变化时才更新
        if (needUpdate) {
            log.info("用户信息有变化，更新用户: userId={}", existingUser.getId());
            UserUpdateReqVO updateReqVO = new UserUpdateReqVO();
            updateReqVO.setId(existingUser.getId());
            updateReqVO.setUsername(existingUser.getUsername());
            updateReqVO.setNickname(userInfo.getNickname());
            updateReqVO.setEmail(userInfo.getEmail());
            updateReqVO.setMobile(userInfo.getMobile());
            updateReqVO.setUserType(existingUser.getUserType());
            userService.updateUser(updateReqVO);
            // 更新返回对象
            existingUser.setNickname(userInfo.getNickname());
            existingUser.setEmail(userInfo.getEmail());
            existingUser.setMobile(userInfo.getMobile());
        } else {
            log.debug("用户信息无变化，跳过更新: userId={}", existingUser.getId());
        }

        return existingUser;
    }

    private AdminUserDO findExistingUser(AdminUserDO userInfo) {
        AdminUserDO existingUser = userService.getUserByUsername(userInfo.getUsername());
        if (existingUser != null) {
            return existingUser;
        }
        if (StringUtils.isBlank(userInfo.getMobile())) {
            return null;
        }
        existingUser = userService.getUserByMobile(userInfo.getMobile(), UserTypeEnum.TENANT.getValue());
        if (existingUser != null) {
            return existingUser;
        }
        return userService.getUserByMobile(userInfo.getMobile(), UserTypeEnum.CORP.getValue());
    }

    private boolean isUserConflictException(ServiceException ex) {
        if (ex == null || ex.getCode() == null) {
            return false;
        }
        return Objects.equals(ex.getCode(), USER_USERNAME_EXISTS.getCode())
                || Objects.equals(ex.getCode(), USER_MOBILE_EXISTS.getCode())
                || Objects.equals(ex.getCode(), USER_EMAIL_EXISTS.getCode());
    }

    /**
     * 创建新用户
     */
    private AdminUserDO createUser(AdminUserDO userInfo) {
        UserInsertReqVO insertReqVO = new UserInsertReqVO();
        insertReqVO.setUsername(userInfo.getUsername());
        insertReqVO.setNickname(userInfo.getNickname());
        insertReqVO.setMobile(userInfo.getMobile());
        insertReqVO.setEmail(userInfo.getEmail());
        insertReqVO.setUserType(UserTypeEnum.TENANT.getValue());
        insertReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        insertReqVO.setPassword("AdminChina2025!");

        // 设置租户管理员角色
        RoleDO tenantAdminRole = roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
        if (tenantAdminRole != null) {
            insertReqVO.setRoleIds(Set.of(tenantAdminRole.getId()));
            log.info("为用户设置租户管理员角色: username={}, roleId={}", userInfo.getUsername(), tenantAdminRole.getId());
        } else {
            log.warn("未找到租户管理员角色，用户将没有角色: username={}", userInfo.getUsername());
        }

        Long userId = createUserWithFallbackOperator(insertReqVO);
        userInfo.setId(userId);
        return userInfo;
    }

    private Long createUserWithFallbackOperator(UserInsertReqVO insertReqVO) {
        if (SecurityFrameworkUtils.getLoginUserId() != null) {
            return userService.createUser(insertReqVO);
        }

        Authentication previousAuthentication = SecurityFrameworkUtils.getAuthentication();
        try {
            LoginUser fallbackLoginUser = new LoginUser();
            fallbackLoginUser.setId(SSO_FALLBACK_OPERATOR_USER_ID);
            fallbackLoginUser.setUserType(UserTypeEnum.PLATFORM.getValue());

            UsernamePasswordAuthenticationToken fallbackAuthentication =
                    new UsernamePasswordAuthenticationToken(fallbackLoginUser, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(fallbackAuthentication);

            return userService.createUser(insertReqVO);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
        }
    }

    /**
     * 登录成功后创建 Token
     */
    private AuthLoginRespVO createTokenAfterLoginSuccess(AdminUserDO adminUser, String deviceId, TenantDO tenantDo) {
        // 插入登陆日志
        createLoginLog(adminUser.getId(), adminUser.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.SUCCESS);

        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessTokenWithMode(
            RunModeEnum.BUILD.getValue(), adminUser.getCorpId(), null,
            adminUser.getId(), adminUser.getUserType(),
            OAuth2ClientConstants.CLIENT_ID_DEFAULT, null, null
        );

        // 检查并限制设备数，踢出超限的设备
        List<String> removedTokens = securityConfigApi.checkAndLimitDevices(adminUser.getId(), deviceId, accessTokenDO.getAccessToken()).getData();
        if (removedTokens != null && !removedTokens.isEmpty()) {
            for (String removedToken : removedTokens) {
                oauth2TokenService.removeAccessToken(removedToken);
                log.info("灵畿SSO登录设备数超限，已踢出Token: userId={}, token={}", adminUser.getId(), removedToken);
            }
        }

        // 构建返回结果
        AuthLoginRespVO respVO = AuthConvert.INSTANCE.convert(accessTokenDO, tenantDo);
        respVO.setAdminFlag(findTenantAdminFlag(adminUser.getId(), tenantDo.getId()));
        PasswordExpiryCheckDTO expiryCheckResult = securityConfigApi.checkPasswordExpiry(adminUser.getId()).getData();
        respVO.setPasswordExpiryInfo(expiryCheckResult);
        securityConfigApi.createSessionIdleKey(adminUser.getId(), deviceId);
        return respVO;
    }

    private Boolean findTenantAdminFlag(Long userId, Long tenantId) {
        RoleDO roleDO = roleService.getRoleIdsByCodeAndTenantId(RoleCodeEnum.TENANT_ADMIN.getCode(), tenantId);
        if (roleDO == null) {
            return false;
        }
        return userService.findAdminByRoleIdAndUserId(roleDO.getId(), userId);
    }

    /**
     * 创建登录日志
     */
    private void createLoginLog(Long userId, String username, LoginLogTypeEnum logType, LoginResultEnum loginResult) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType.getType());
        reqDTO.setTraceId("n/a");
        reqDTO.setUserId(userId);
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogService.createLoginLog(reqDTO);
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }

    private String normalizeDeviceId(String deviceId) {
        if (StringUtils.isNotBlank(deviceId)) {
            return deviceId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
