package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity.SecurityConfigDO;
import com.cmsr.onebase.module.infra.dal.mapper.ssecurity.SecurityConfigMapper;
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
}
