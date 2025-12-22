package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2RefreshTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO.REFRESH_TOKEN;

/**
 * OAuth2 刷新令牌数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2RefreshTokenDataRepository extends BaseDataServiceImpl<SystemOauth2RefreshTokenMapper, OAuth2RefreshTokenDO> {

    /**
     * 根据刷新令牌查找 OAuth2 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2 刷新令牌
     */
    public OAuth2RefreshTokenDO findOneByRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return null;
        }
        return getOne(query().eq(REFRESH_TOKEN, refreshToken));
    }

    /**
     * 根据刷新令牌删除 OAuth2 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 删除数量
     */
    public long deleteByRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(REFRESH_TOKEN, refreshToken));
    }
}
