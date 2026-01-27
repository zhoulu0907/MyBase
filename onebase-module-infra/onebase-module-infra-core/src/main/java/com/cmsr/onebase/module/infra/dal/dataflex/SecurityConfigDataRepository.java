package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity.SecurityConfigDO;
import com.cmsr.onebase.module.infra.dal.mapper.ssecurity.SecurityConfigMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Repository;

/**
 * 安全配置数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigDataRepository extends BaseDataRepository<SecurityConfigMapper, SecurityConfigDO> {

    /**
     * 根据租户ID和配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    public SecurityConfigDO findByKey(String configKey) {
        return getOne(query().eq(SecurityConfigDO.CONFIG_KEY, configKey));
    }

    /**
     * 根据租户ID逻辑删除配置记录
     *
     * @param tenantId 租户ID
     * @return 删除记录数量
     */
    public long deleteByTenantId(Long tenantId) {
        QueryWrapper queryWrapper = query().eq(SecurityConfigDO.TENANT_ID, tenantId);
        return remove(queryWrapper) ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }
}