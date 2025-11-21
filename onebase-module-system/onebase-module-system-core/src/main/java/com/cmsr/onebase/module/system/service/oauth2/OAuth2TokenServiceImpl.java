package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.cmsr.onebase.module.system.dal.database.OAuth2AccessTokenDataRepository;
import com.cmsr.onebase.module.system.dal.database.OAuth2RefreshTokenDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.cmsr.onebase.module.system.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * OAuth2.0 Token Service 实现类
 *
 */
@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Resource
    private OAuth2ClientService oauth2ClientService;
    @Resource
    @Lazy // 懒加载，避免循环依赖
    private UserService         userService;

    @Resource
    private OAuth2AccessTokenDataRepository  oauth2AccessTokenDataRepository;
    @Resource
    private OAuth2RefreshTokenDataRepository oauth2RefreshTokenDataRepository;
    @Resource
    private com.cmsr.onebase.module.infra.api.security.SecurityConfigApi securityConfigApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(null, null, userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO createAppAccessToken(Long appId, Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(null, appId, userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO createCorpAccessToken(Long corpId, Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(corpId, null, userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        // 查询访问令牌
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenDataRepository.findOneByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }

        // 校验 Client 匹配
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        if (ObjUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 移除相关的访问令牌，并反查deviceId
        String deviceId = null;
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenDataRepository.findListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            // 通过旧Token反查deviceId
            OAuth2AccessTokenDO oldToken = accessTokenDOs.get(0);
            CommonResult<String> deviceIdResult = securityConfigApi.findDeviceIdByToken(
                    oldToken.getUserId(),
                    oldToken.getAccessToken()
            );
            if (deviceIdResult != null && deviceIdResult.getData() != null) {
                deviceId = deviceIdResult.getData();
            }
            
            List<Long> ids = accessTokenDOs.stream().map(OAuth2AccessTokenDO::getId).collect(Collectors.toUnmodifiableList());
            oauth2AccessTokenDataRepository.deleteByIds(ids);
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
            
            // 删除在线设备记录中的旧Token
            for (OAuth2AccessTokenDO accessToken : accessTokenDOs) {
                securityConfigApi.removeOnlineDevice(
                        accessToken.getUserId(),
                        accessToken.getAccessToken()
                );
            }
        }

        // 已过期的情况下，删除刷新令牌
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenDataRepository.deleteById(refreshTokenDO.getId());
            throw exception(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }

        // 创建访问令牌
        OAuth2AccessTokenDO newAccessTokenDO = createOAuth2AccessToken(refreshTokenDO, clientDO);
        
        // 将新Token添加到在线设备记录：如果反查到deviceId则使用，否则直接存储新Token
        if (StrUtil.isNotBlank(deviceId)) {
            securityConfigApi.addOnlineDevice(
                    newAccessTokenDO.getUserId(),
                    deviceId,
                    newAccessTokenDO.getAccessToken()
            );
        }
        
        return newAccessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // 获取不到，从 MySQL 中获取访问令牌
        accessTokenDO = oauth2AccessTokenDataRepository.findByAccessToken(accessToken);
        if (accessTokenDO == null) {
            // 特殊：从 MySQL 中获取刷新令牌。原因：解决部分场景不方便刷新访问令牌场景
            // 例如说，积木报表只允许传递 token，不允许传递 refresh_token，导致无法刷新访问令牌
            // 再例如说，前端 WebSocket 的 token 直接跟在 url 上，无法传递 refresh_token
            OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenDataRepository.findOneByRefreshToken(accessToken);
            if (refreshTokenDO != null && !DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
                accessTokenDO = convertToAccessToken(refreshTokenDO);
            }
        }

        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenDataRepository.findByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }

        oauth2AccessTokenDataRepository.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // 删除刷新令牌
        oauth2RefreshTokenDataRepository.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public PageResult<OAuth2AccessTokenDO> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO) {
        return oauth2AccessTokenDataRepository.findPage(reqVO);
    }

    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(generateAccessToken())
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setCorpId(refreshTokenDO.getCorpId())
                .setAppId(refreshTokenDO.getAppId())
                .setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType()))
                .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
        accessTokenDO.setTenantId(TenantContextHolder.getTenantId()); // 手动设置租户编号，避免缓存到 Redis 的时候，无对应的租户编号
        oauth2AccessTokenDataRepository.insert(accessTokenDO);
        // 记录到 Redis 中
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(Long corpId, Long appId, Long userId, Integer userType, OAuth2ClientDO clientDO, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO().setRefreshToken(generateRefreshToken())
                .setUserId(userId).setUserType(userType)
                .setCorpId(corpId)
                .setAppId(appId)
                .setClientId(clientDO.getClientId()).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));

        oauth2RefreshTokenDataRepository.insert(refreshToken);
        return refreshToken;
    }

    private OAuth2AccessTokenDO convertToAccessToken(OAuth2RefreshTokenDO refreshTokenDO) {
        OAuth2AccessTokenDO accessTokenDO = BeanUtils.toBean(refreshTokenDO, OAuth2AccessTokenDO.class)
                .setAccessToken(refreshTokenDO.getRefreshToken());
        TenantUtils.execute(refreshTokenDO.getTenantId(),
                () -> accessTokenDO.setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType())));
        return accessTokenDO;
    }

    /**
     * 加载用户信息，方便 {@link com.cmsr.onebase.framework.security.core.LoginUser} 获取到昵称、部门等信息
     *
     * @param userId   用户编号
     * @param userType 用户类型
     * @return 用户信息
     */
    private Map<String, String> buildUserInfo(Long userId, Integer userType) {
        if (userType.equals(UserTypeEnum.THIRD.getValue())) {
            AdminUserDO user = userService.getUser(userId);
            return MapUtil.builder(LoginUser.INFO_KEY_NICKNAME, user.getNickname())
                    .put(LoginUser.INFO_KEY_DEPT_ID, StrUtil.toStringOrNull(user.getDeptId())).build();
        } else if (userType.equals(UserTypeEnum.CORP.getValue())) {
            // 注意：目前 Member 暂时不读取，可以按需实现
            return Collections.emptyMap();
        }
        return null;
    }

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

}
