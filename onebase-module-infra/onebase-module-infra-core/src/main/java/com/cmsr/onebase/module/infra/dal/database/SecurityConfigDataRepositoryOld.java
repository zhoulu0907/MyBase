package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 安全配置数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigDataRepositoryOld extends DataRepository<SecurityConfigDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public SecurityConfigDataRepositoryOld() {
        super(SecurityConfigDO.class);
    }

    /**
     * 根据租户ID和配置键查询配置
     *
     * @param tenantId  租户ID
     * @param configKey 配置键
     * @return 配置对象
     */
    public SecurityConfigDO findByTenantIdAndKey(Long tenantId, String configKey) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityConfigDO.CONFIG_KEY, configKey);
        return findOne(configStore);
    }

    public SecurityConfigDO findSecurityConfigByTenantIdAndKey(Long tenantId, String securityConfigKey) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityConfigDO.TENANT_ID, tenantId);
        configStore.eq(SecurityConfigDO.CONFIG_KEY, securityConfigKey);
        return findOne(configStore);
    }
}
