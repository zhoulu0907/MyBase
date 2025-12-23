package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2AccessTokenMapper;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.ACCESS_TOKEN;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.CLIENT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.REFRESH_TOKEN;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.USER_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO.USER_TYPE;

/**
 * OAuth2 访问令牌数据访问层
 *
 * 注意：历史类名为 OAuth2TokenDataRepository，但实际实体为 OAuth2AccessTokenDO。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2TokenDataRepository extends BaseDataServiceImpl<SystemOauth2AccessTokenMapper, OAuth2AccessTokenDO> {

    /**
     * 根据访问令牌查找 OAuth2 访问令牌
     *
     * @param accessToken 访问令牌
     * @return OAuth2 访问令牌
     */
    public OAuth2AccessTokenDO findOneByAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        return getOne(query().eq(ACCESS_TOKEN, accessToken));
    }

    /**
     * 根据刷新令牌查找 OAuth2 访问令牌列表
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2 访问令牌列表
     */
    public List<OAuth2AccessTokenDO> findListByRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return Collections.emptyList();
        }
        return list(query().eq(REFRESH_TOKEN, refreshToken));
    }

    /**
     * 分页查询 OAuth2 访问令牌
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<OAuth2AccessTokenDO> findPage(OAuth2AccessTokenPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .eq(USER_ID, reqVO.getUserId(), reqVO.getUserId() != null)
                .eq(USER_TYPE, reqVO.getUserType(), reqVO.getUserType() != null)
                .eq(CLIENT_ID, reqVO.getClientId(), StringUtils.isNotBlank(reqVO.getClientId()));

        Page<OAuth2AccessTokenDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
