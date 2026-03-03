package com.cmsr.onebase.module.system.service.oauth2;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

import java.time.LocalDateTime;
import java.util.List;

import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.module.system.dal.database.OAuth2CodeDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2CodeDO;

import jakarta.annotation.Resource;

/**
 * OAuth2.0 授权码 Service 实现类
 *
 */
@Service
@Validated
public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    /**
     * 授权码的过期时间，默认 5 分钟
     */
    private static final Integer TIMEOUT = 5 * 60;

    @Resource
    private OAuth2CodeDataRepository oauth2CodeDataRepository;

    @Override
    public OAuth2CodeDO createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeDO codeDO = new OAuth2CodeDO().setCode(generateCode())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(TIMEOUT))
                .setRedirectUri(redirectUri).setState(state);
        oauth2CodeDataRepository.insert(codeDO);
        return codeDO;
    }

    @Override
    public OAuth2CodeDO consumeAuthorizationCode(String code) {
        OAuth2CodeDO codeDO = oauth2CodeDataRepository.findOneByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateUtils.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        // 删除授权码，避免被二次使用  todo 调试时暂时注掉该代码方便调试,待联调完成后恢复
//        oauth2CodeDataRepository.deleteById(codeDO.getId());
        return codeDO;
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

}
