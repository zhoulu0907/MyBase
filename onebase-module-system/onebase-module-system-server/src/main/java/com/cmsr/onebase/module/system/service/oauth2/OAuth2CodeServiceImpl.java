package com.cmsr.onebase.module.system.service.oauth2;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

import java.time.LocalDateTime;
import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2CodeDO;

import cn.hutool.core.util.IdUtil;
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
    private DataRepository dataRepository;

    @Override
    public OAuth2CodeDO createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeDO codeDO = new OAuth2CodeDO().setCode(generateCode())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(TIMEOUT))
                .setRedirectUri(redirectUri).setState(state);
        dataRepository.insert(codeDO);
		//oauth2CodeMapper.insert(codeDO);
        return codeDO;
    }

    @Override
    public OAuth2CodeDO consumeAuthorizationCode(String code) {

        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code);

        OAuth2CodeDO codeDO = dataRepository.findOne(OAuth2CodeDO.class,cs);

        //OAuth2CodeDO codeDO = oauth2CodeMapper.selectByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateUtils.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        dataRepository.deleteById(OAuth2CodeDO.class,codeDO.getId());
		//oauth2CodeMapper.deleteById(codeDO.getId());
        return codeDO;
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

}
