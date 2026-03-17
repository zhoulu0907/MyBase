package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientExternalConfigDO;

/**
 * OAuth2 外部客户端配置 Service 接口
 *
 */
public interface OAuth2ClientExternalConfigService {

    /**
     * 根据租户编码获取 OAuth2 外部客户端配置
     *
     * @param tenantCode 租户编码
     * @return OAuth2 外部客户端配置
     */
    OAuth2ClientExternalConfigDO getConfigByTenantCode(String tenantCode);

    /**
     * 更新企业租户映射配置
     *
     * @param id 配置ID
     * @param enterpriseMapping 企业租户映射JSON
     */
    void updateEnterpriseMapping(Long id, String enterpriseMapping);

}
