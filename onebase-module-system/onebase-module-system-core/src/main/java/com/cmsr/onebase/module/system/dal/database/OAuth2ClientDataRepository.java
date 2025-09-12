package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2ClientPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

/**
 * OAuth2客户端数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class OAuth2ClientDataRepository extends DataRepository<OAuth2ClientDO> {

    public OAuth2ClientDataRepository() {
        super(OAuth2ClientDO.class);
    }

    /**
     * 根据客户端ID查找OAuth2客户端
     *
     * @param clientId 客户端ID
     * @return OAuth2客户端
     */
    public OAuth2ClientDO findOneByClientId(String clientId) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, OAuth2ClientDO.CLIENT_ID, clientId));
    }

    /**
     * 分页查询OAuth2客户端
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<OAuth2ClientDO> findPage(OAuth2ClientPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configStore.and(Compare.LIKE, OAuth2ClientDO.NAME, pageReqVO.getName());
        }
        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, OAuth2ClientDO.STATUS, pageReqVO.getStatus());
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
