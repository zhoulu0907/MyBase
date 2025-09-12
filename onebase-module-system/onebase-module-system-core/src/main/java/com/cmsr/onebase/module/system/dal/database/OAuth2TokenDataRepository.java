package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * OAuth2访问令牌数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class OAuth2TokenDataRepository extends DataRepository<OAuth2AccessTokenDO> {

    public OAuth2TokenDataRepository() {
        super(OAuth2AccessTokenDO.class);
    }

    /**
     * 根据访问令牌查找OAuth2访问令牌
     *
     * @param accessToken 访问令牌
     * @return OAuth2访问令牌
     */
    public OAuth2AccessTokenDO findOneByAccessToken(String accessToken) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, OAuth2AccessTokenDO.ACCESS_TOKEN, accessToken));
    }

    /**
     * 根据刷新令牌查找OAuth2访问令牌列表
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2访问令牌列表
     */
    public List<OAuth2AccessTokenDO> findListByRefreshToken(String refreshToken) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, OAuth2AccessTokenDO.REFRESH_TOKEN, refreshToken));
    }

    /**
     * 分页查询OAuth2访问令牌
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<OAuth2AccessTokenDO> findPage(OAuth2AccessTokenPageReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != reqVO.getUserId()) {
            configStore.and(Compare.EQUAL, OAuth2AccessTokenDO.USER_ID, reqVO.getUserId());
        }
        if (null != reqVO.getUserType()) {
            configStore.and(Compare.EQUAL, OAuth2AccessTokenDO.USER_TYPE, reqVO.getUserType());
        }
        if (StringUtils.isNotBlank(reqVO.getClientId())) {
            configStore.and(Compare.EQUAL, OAuth2AccessTokenDO.CLIENT_ID, reqVO.getClientId());
        }

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
