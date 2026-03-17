package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.external.SystemExternalUserDO;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemExternalUserMapper;
import org.springframework.stereotype.Repository;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 外部系统用户数据访问
 *
 * @author matianyu
 * @date 2026-03-17
 */
@Repository
public class SystemExternalUserDataRepository extends BaseDataRepository<SystemExternalUserMapper, SystemExternalUserDO> {

    /**
     * 根据 onebase 用户 id 查询外部用户关联
     *
     * @param obUserId onebase 用户 id
     * @return 记录列表
     */
    public List<SystemExternalUserDO> findByObUserId(String obUserId) {
        if (obUserId == null) {
            return List.of();
        }
        return list(query().eq(SystemExternalUserDO.OB_USER_ID, obUserId));
    }

    /**
     * 根据外部用户 id 查询关联
     *
     * @param externalUserId 外部用户 id
     * @return 记录列表
     */
    public SystemExternalUserDO findByExternalUserId(String externalUserId, String platformType, String externalTenantId) {

        return getOne(query().eq(SystemExternalUserDO.EXTERNAL_USER_ID, externalUserId).eq(SystemExternalUserDO.EXTERNAL_TENANT_ID, externalTenantId).eq(SystemExternalUserDO.PLATFORM_TYPE, platformType));
    }

    /**
     * 根据 onebase 租户 id 查询
     */
    public List<SystemExternalUserDO> findByObTenantId(String obTenantId) {
        if (obTenantId == null) {
            return List.of();
        }
        return list(query().eq(SystemExternalUserDO.OB_TENANT_ID, obTenantId));
    }

}

