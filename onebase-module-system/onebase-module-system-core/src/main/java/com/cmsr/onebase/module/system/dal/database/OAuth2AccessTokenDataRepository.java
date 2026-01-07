package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2AccessTokenMapper;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.ACCESS_TOKEN;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.CLIENT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.REFRESH_TOKEN;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.RUN_MODE;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.USER_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.USER_TYPE;

/**
 * OAuth2 访问令牌数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2AccessTokenDataRepository extends BaseDataRepository<SystemOauth2AccessTokenMapper, OAuth2AccessTokenDO> {

    public OAuth2AccessTokenDO findByAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        return getOne(query().eq(ACCESS_TOKEN, accessToken));
    }

    public OAuth2AccessTokenDO findByAccessTokenWithMode(String runMode, String accessToken) {
        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(runMode)) {
            return null;
        }
        return getOne(query().eq(RUN_MODE, runMode).eq(ACCESS_TOKEN, accessToken));
    }

    public List<OAuth2AccessTokenDO> findListByRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return Collections.emptyList();
        }
        return list(query().eq(REFRESH_TOKEN, refreshToken));
    }

    public long deleteByIds(Collection<Long> ids) {
        return super.deleteByIds(ids);
    }

    public PageResult<OAuth2AccessTokenDO> findPage(OAuth2AccessTokenPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .eq(USER_ID, reqVO.getUserId(), reqVO.getUserId() != null)
                .eq(USER_TYPE, reqVO.getUserType(), reqVO.getUserType() != null)
                .eq(CLIENT_ID, reqVO.getClientId(), StringUtils.isNotBlank(reqVO.getClientId()));

        Page<OAuth2AccessTokenDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
