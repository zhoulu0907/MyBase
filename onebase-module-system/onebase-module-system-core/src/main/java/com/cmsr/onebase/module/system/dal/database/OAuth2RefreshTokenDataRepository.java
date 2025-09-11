package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * OAuth2刷新令牌数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class OAuth2RefreshTokenDataRepository extends DataRepository<OAuth2RefreshTokenDO> {

    public OAuth2RefreshTokenDataRepository() {
        super(OAuth2RefreshTokenDO.class);
    }

    /**
     * 根据刷新令牌查找OAuth2刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2刷新令牌
     */
    public OAuth2RefreshTokenDO findOneByRefreshToken(String refreshToken) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, OAuth2RefreshTokenDO.REFRESH_TOKEN, refreshToken));
    }

    /**
     * 根据刷新令牌删除OAuth2刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 删除数量
     */
    public long deleteByRefreshToken(String refreshToken) {
        return deleteByConfig(new DefaultConfigStore().and(Compare.EQUAL, OAuth2RefreshTokenDO.REFRESH_TOKEN, refreshToken));
    }
}
