package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientOutConfigDO;
import com.cmsr.onebase.module.system.dal.database.OAuth2ClientOutConfigDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OAuth2 外部客户端配置 Service 实现类
 *
 */
@Service
@Slf4j
public class OAuth2ClientOutConfigServiceImpl implements OAuth2ClientOutConfigService {

    @Resource
    private OAuth2ClientOutConfigDataRepository oauth2ClientOutConfigDataRepository;

    @Override
    public OAuth2ClientOutConfigDO getConfigByTenantCode(String tenantCode) {
        if (tenantCode == null) {
            return null;
        }
        return oauth2ClientOutConfigDataRepository.findOneByTenantCode(tenantCode);
    }

    @Override
    public void updateEnterpriseMapping(Long id, String enterpriseMapping) {
        OAuth2ClientOutConfigDO config = oauth2ClientOutConfigDataRepository.getById(id);
        if (config != null) {
            config.setEnterpriseMapping(enterpriseMapping);
            oauth2ClientOutConfigDataRepository.update(config);
            log.info("更新企业租户映射配置: id={}, mapping={}", id, enterpriseMapping);
        }
    }

}
