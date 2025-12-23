package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2ClientMapper;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2ClientPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO.CLIENT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO.NAME;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO.STATUS;

/**
 * OAuth2 客户端数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2ClientDataRepository extends BaseDataServiceImpl<SystemOauth2ClientMapper, OAuth2ClientDO> {

    /**
     * 根据客户端ID查找 OAuth2 客户端
     *
     * @param clientId 客户端ID
     * @return OAuth2 客户端
     */
    public OAuth2ClientDO findOneByClientId(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }
        return getOne(query().eq(CLIENT_ID, clientId));
    }

    /**
     * 分页查询 OAuth2 客户端
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<OAuth2ClientDO> findPage(OAuth2ClientPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .like(NAME, pageReqVO.getName(), StringUtils.isNotBlank(pageReqVO.getName()))
                .eq(STATUS, pageReqVO.getStatus(), pageReqVO.getStatus() != null);

        Page<OAuth2ClientDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
