package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * OAuth2访问令牌 DataRepository
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Repository
public class OAuth2AccessTokenDataRepository extends DataRepository<OAuth2AccessTokenDO> {

    public OAuth2AccessTokenDataRepository() {
        super(OAuth2AccessTokenDO.class);
    }

    public OAuth2AccessTokenDO findByAccessToken(String accessToken) {
        return findOne(new DefaultConfigStore().eq(OAuth2AccessTokenDO.ACCESS_TOKEN, accessToken));
    }

    public OAuth2AccessTokenDO findByAccessTokenWithMode(String runMode, String accessToken) {
        return findOne(new DefaultConfigStore()
                .eq(OAuth2AccessTokenDO.RUN_MODE, runMode)
                .eq(OAuth2AccessTokenDO.ACCESS_TOKEN, accessToken));
    }

    public List<OAuth2AccessTokenDO> findListByRefreshToken(String refreshToken) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, "refresh_token", refreshToken));
    }

    public long deleteByIds(Collection<Long> ids) {
        return deleteByConfig(new DefaultConfigStore()
                .in("id", ids));
    }

    public PageResult<OAuth2AccessTokenDO> findPage(OAuth2AccessTokenPageReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (reqVO.getUserId() != null) {
            configStore.and(Compare.EQUAL, "user_id", reqVO.getUserId());
        }
        if (reqVO.getUserType() != null) {
            configStore.and(Compare.EQUAL, "user_type", reqVO.getUserType());
        }
        if (StringUtils.isNotBlank(reqVO.getClientId())) {
            configStore.and(Compare.EQUAL, "client_id", reqVO.getClientId());
        }

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }
}

