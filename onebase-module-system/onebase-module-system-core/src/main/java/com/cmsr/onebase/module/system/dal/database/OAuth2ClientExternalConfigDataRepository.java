package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientExternalConfigDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2ClientExternalConfigMapper;
import org.springframework.stereotype.Repository;

/**
 * OAuth2 外部客户端配置数据访问层
 *
 */
@Repository
public class OAuth2ClientExternalConfigDataRepository extends BaseDataRepository<SystemOauth2ClientExternalConfigMapper, OAuth2ClientExternalConfigDO> {

    /**
     * 根据租户编码查找 OAuth2 外部客户端配置
     *
     * @param tenantCode 租户编码
     * @return OAuth2 外部客户端配置
     */
    public OAuth2ClientExternalConfigDO findOneByTenantCode(String tenantCode) {
        if (tenantCode == null) {
            return null;
        }
        return getOne(query().eq(OAuth2ClientExternalConfigDO.TENANT_CODE, tenantCode));
    }

}
