package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全配置数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigDataRepository extends DataRepository<SecurityConfigDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public SecurityConfigDataRepository() {
        super(SecurityConfigDO.class);
    }

    /**
     * 根据租户ID查询配置
     *
     * @param tenantId 租户ID
     * @return 配置列表
     */
    public List<SecurityConfigDO> findByTenantId(Long tenantId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityConfigDO.TENANT_ID, tenantId);
        return findAllByConfig(configStore);
    }

    /**
     * 根据租户ID和配置键列表查询配置
     *
     * @param tenantId   租户ID
     * @param configKeys 配置键列表
     * @return 配置列表
     */
    public List<SecurityConfigDO> findByTenantIdAndKeys(Long tenantId, List<String> configKeys) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(SecurityConfigDO.TENANT_ID, tenantId);
        configStore.in(SecurityConfigDO.CONFIG_KEY, configKeys);
        return findAllByConfig(configStore);
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
        configStore.eq(SecurityConfigDO.TENANT_ID, tenantId);
        configStore.eq(SecurityConfigDO.CONFIG_KEY, configKey);
        return findOne(configStore);
    }

}
