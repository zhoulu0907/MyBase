package com.cmsr.onebase.module.system.service.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
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
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserUpdateReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    /**
     * 企业ID到租户ID的映射（内存缓存）
     * TODO: 如果需要持久化，可以存到数据库或 Redis
     */
    private final Map<String, Long> enterpriseTenantMapping = new ConcurrentHashMap<>();

    /**
     * 默认租户套餐编码
     */
    private static final String DEFAULT_PACKAGE_CODE = "default";

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

        // 5. 处理用户信息并登录
        return processUserLogin(userInfo, deviceId, tenantDo);
    }

    /**
     * 获取灵畿用户信息
     */
    private LingjiSsoUserInfoVO getUserInfo(String code) {
        // 生成时间戳 (格式: yyyyMMddHHmmssSSS)
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

        // 检查响应状态
        String status = responseObj.getStr("status");
        if (!"0".equals(status)) {
            log.error("灵畿SSO获取用户信息失败: status={}, message={}", status, responseObj.getStr("message"));
            throw exception(LINGJI_SSO_GET_TOKEN_FAILED);
        }

        // 获取 id_token
        String idToken = responseObj.getStr("id_token");
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
     * 查找租户
     */
    private TenantDO findTenant(String enterpriseId) {
        // 从内存映射中查找租户ID
        Long tenantId = enterpriseTenantMapping.get(enterpriseId);
        if (tenantId == null) {
            return null;
        }
        return tenantService.getTenant(tenantId);
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
        tenantReqVO.setName(buildTenantName(userInfo, enterpriseId));
        tenantReqVO.setTenantCode(buildTenantCode(enterpriseId));
        tenantReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        tenantReqVO.setPackageId(tenantPackage.getId());
        tenantReqVO.setExpireTime(LocalDateTime.now().plusYears(10)); // 默认10年
        tenantReqVO.setAccountCount(10000); // 默认10000个账号

        // 设置管理员信息（当前登录用户）
        TenantAdminUserReqVO adminReqVO = new TenantAdminUserReqVO();
        adminReqVO.setAdminUserName(StringUtils.isNotBlank(userInfo.getStaffCode()) ? userInfo.getStaffCode() : userInfo.getSub());
        adminReqVO.setAdminNickName(StringUtils.isNotBlank(userInfo.getUserName()) ? userInfo.getUserName() : adminReqVO.getAdminUserName());
        adminReqVO.setAdminMobile(userInfo.getSub());
        adminReqVO.setAdminEmail(userInfo.getEmail());
        tenantReqVO.setTenantAdminUserReqVOList(Collections.singletonList(adminReqVO));

        // 创建租户
        Long tenantId = tenantService.createTenant(tenantReqVO);
        log.info("创建租户成功: tenantId={}, enterpriseId={}, tenantCode={}", tenantId, enterpriseId, tenantReqVO.getTenantCode());

        // 更新内存映射
        enterpriseTenantMapping.put(enterpriseId, tenantId);
        log.info("更新企业租户映射: enterpriseId={} -> tenantId={}", enterpriseId, tenantId);

        return tenantService.getTenant(tenantId);
    }

    /**
     * 构建租户名称
     */
    private String buildTenantName(LingjiSsoUserInfoVO userInfo, String enterpriseId) {
        if (StringUtils.isNotBlank(userInfo.getUserName())) {
            return userInfo.getUserName() + "的空间";
        }
        return "企业" + enterpriseId + "的空间";
    }

    /**
     * 构建租户编码
     */
    private String buildTenantCode(String enterpriseId) {
        return "lingji_" + enterpriseId + "_" + System.currentTimeMillis();
    }

    /**
     * 处理用户登录
     */
    private AuthLoginRespVO processUserLogin(LingjiSsoUserInfoVO userInfo, String deviceId, TenantDO tenantDo) {
        // 构建用户对象
        AdminUserDO adminUserDO = new AdminUserDO();
        adminUserDO.setMobile(userInfo.getSub()); // 手机号在 sub 字段
        adminUserDO.setNickname(userInfo.getUserName());
        adminUserDO.setUsername(StringUtils.isNotBlank(userInfo.getStaffCode()) ? userInfo.getStaffCode() : userInfo.getSub());
        adminUserDO.setEmail(userInfo.getEmail());
        adminUserDO.setUserType(UserTypeEnum.CORP.getValue());

        if (StringUtils.isBlank(adminUserDO.getNickname())) {
            adminUserDO.setNickname(adminUserDO.getUsername());
        }

        // 保存用户信息并创建 Token
        AtomicReference<AuthLoginRespVO> loginRespRef = new AtomicReference<>();
        TenantUtils.execute(tenantDo.getId(), () -> {
            // 查找或创建用户
            AdminUserDO adminUser = findOrCreateUser(adminUserDO, userInfo);
            // 创建 Token
            AuthLoginRespVO authLoginRespVO = createTokenAfterLoginSuccess(
                adminUser.getUserType(),
                adminUser.getId(),
                adminUser.getUsername(),
                deviceId
            );
            loginRespRef.set(authLoginRespVO);
        });

        AuthLoginRespVO authLoginRespVO = loginRespRef.get();
        if (authLoginRespVO == null) {
            throw exception(LINGJI_SSO_CREATE_TOKEN_FAILED);
        }
        // 设置租户ID
        authLoginRespVO.setTenantId(tenantDo.getId());
        return authLoginRespVO;
    }

    /**
     * 查找或创建用户（只有关键信息变化时才更新）
     */
    private AdminUserDO findOrCreateUser(AdminUserDO userInfo, LingjiSsoUserInfoVO ssoUserInfo) {
        // 通过用户名查找用户
        AdminUserDO existingUser = userService.getUserByUsername(userInfo.getUsername());

        if (existingUser == null) {
            // 用户不存在，创建新用户
            log.info("用户不存在，创建新用户: username={}", userInfo.getUsername());
            return createUser(userInfo);
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

        // 只有变化时才更新
        if (needUpdate) {
            log.info("用户信息有变化，更新用户: userId={}", existingUser.getId());
            UserUpdateReqVO updateReqVO = new UserUpdateReqVO();
            updateReqVO.setId(existingUser.getId());
            updateReqVO.setUsername(existingUser.getUsername());
            updateReqVO.setNickname(userInfo.getNickname());
            updateReqVO.setEmail(userInfo.getEmail());
            updateReqVO.setMobile(userInfo.getMobile());
            userService.updateUser(updateReqVO);
            // 更新返回对象
            existingUser.setNickname(userInfo.getNickname());
            existingUser.setEmail(userInfo.getEmail());
        } else {
            log.debug("用户信息无变化，跳过更新: userId={}", existingUser.getId());
        }

        return existingUser;
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
        insertReqVO.setUserType(UserTypeEnum.CORP.getValue());
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

        Long userId = userService.createUser(insertReqVO);
        userInfo.setId(userId);
        return userInfo;
    }

    /**
     * 登录成功后创建 Token
     */
    private AuthLoginRespVO createTokenAfterLoginSuccess(Integer userType, Long userId, String username, String deviceId) {
        // 插入登陆日志
        createLoginLog(userId, username, LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.SUCCESS);

        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
            userId, userType,
            OAuth2ClientConstants.CLIENT_ID_DEFAULT, null
        );

        // 更新最后登录时间
        userService.updateUserLogin(userId, ServletUtils.getClientIP());

        // 构建返回结果
        AuthLoginRespVO respVO = new AuthLoginRespVO();
        respVO.setAccessToken(accessTokenDO.getAccessToken());
        respVO.setRefreshToken(accessTokenDO.getRefreshToken());
        respVO.setExpiresTime(accessTokenDO.getExpiresTime());

        return respVO;
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
    }
}