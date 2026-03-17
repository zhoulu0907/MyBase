package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientExternalConfigDO;
import com.cmsr.onebase.module.system.dal.database.OAuth2ClientExternalConfigDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OAuth2 外部客户端配置 Service 实现类
 *
 */
@Service
@Slf4j
public class OAuth2ClientExternalConfigServiceImpl implements OAuth2ClientExternalConfigService {

    @Resource
    private OAuth2ClientExternalConfigDataRepository oauth2ClientExternalConfigDataRepository;

    @Override
    public OAuth2ClientExternalConfigDO getConfigByTenantCode(String tenantCode) {
        if (tenantCode == null) {
            return null;
        }
        return oauth2ClientExternalConfigDataRepository.findOneByTenantCode(tenantCode);
    }

    @Override
    public void updateEnterpriseMapping(Long id, String enterpriseMapping) {
        OAuth2ClientExternalConfigDO config = oauth2ClientExternalConfigDataRepository.getById(id);
        if (config != null) {
            config.setEnterpriseMapping(enterpriseMapping);
            oauth2ClientExternalConfigDataRepository.update(config);
            log.info("更新企业租户映射配置: id={}, mapping={}", id, enterpriseMapping);
        }
    }

}
