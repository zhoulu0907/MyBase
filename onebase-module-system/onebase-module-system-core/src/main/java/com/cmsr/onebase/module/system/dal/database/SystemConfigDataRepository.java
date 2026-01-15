package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemConfigMapper;
import com.cmsr.onebase.module.system.enums.config.ConfigTypeEnum;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigSearchVO;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.APP_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.CONFIG_KEY;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.CONFIG_TYPE;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.CORP_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.NAME;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.STATUS;
import static com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO.TENANT_ID;
import static com.cmsr.onebase.module.system.dal.flex.table.SystemConfigTableDef.SYSTEM_CONFIG;

/**
 * 系统参数配置 数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SystemConfigDataRepository extends BaseDataRepository<SystemConfigMapper, SystemGeneralConfigDO> {

    /**
     * 查询租户配置列表
     *
     * @param name       名称（模糊）
     * @param status     状态
     * @param configType 配置类型
     * @return 配置列表
     */
    public List<SystemGeneralConfigDO> findTenantConfigList(String name, Integer status, String configType) {
        QueryWrapper queryWrapper = query()
                .eq(CONFIG_TYPE, ConfigTypeEnum.TENANT.getCode())
                .like(NAME, name, StringUtils.isNotBlank(name))
                .eq(STATUS, status, status != null)
                .eq(CONFIG_TYPE, configType, StringUtils.isNotBlank(configType))
                .orderBy(ID, false);
        List<SystemGeneralConfigDO> list = list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 按不同维度获取配置（忽略租户过滤）
     *
     * @param searchVO 查询条件
     * @return 配置
     */
    @TenantIgnore
    public SystemGeneralConfigDO getConfigByDiffCategory(SystemGeneralConfigSearchVO searchVO) {
        QueryWrapper queryWrapper = query();
        if (StringUtils.isNotBlank(searchVO.getCategory())) {
            queryWrapper.eq(CONFIG_TYPE, searchVO.getCategory());
        }
        if (StringUtils.isNotBlank(searchVO.getConfigKey())) {
            queryWrapper.eq(CONFIG_KEY, searchVO.getConfigKey());
        }
        if (searchVO.getTenantId() != null) {
            queryWrapper.eq(TENANT_ID, searchVO.getTenantId());
        }
        if (searchVO.getCorpId() != null) {
            queryWrapper.eq(CORP_ID, searchVO.getCorpId());
        }
        return getOne(queryWrapper);
    }

    /**
     * 根据配置KEY列表查询全局配置（忽略租户过滤）
     *
     * @param configKeys 配置key列表
     * @return 全局配置列表
     */
    @TenantIgnore
    public List<SystemGeneralConfigDO> findGlobaConfigListByKeys(List<String> configKeys) {
        QueryWrapper queryWrapper = query()
                .eq(CONFIG_TYPE, ConfigTypeEnum.GLOBAL.getCode())
                .in(CONFIG_KEY, configKeys, CollectionUtils.isNotEmpty(configKeys))
                .orderBy(ID, false);
        List<SystemGeneralConfigDO> list = list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 根据配置KEY和应用ID查询
     *
     * @param configKey 配置key
     * @param appId     应用ID
     * @return 配置
     */
    public SystemGeneralConfigDO findOneByConfigKeyAndAppId(String configKey, Long appId) {
        return getOne(query().eq(CONFIG_KEY, configKey)
                .eq(APP_ID, appId));
    }

    /**
     * 根据配置KEY集合、应用ID、配置类型查询
     *
     * @param configKeys 配置key集合
     * @param appId      应用ID
     * @param configType 配置类型
     * @return 配置列表
     */
    public List<SystemGeneralConfigDO> findConfigListByKeysAndAppId(Set<String> configKeys, Long appId, String configType) {
        QueryWrapper queryWrapper = query()
                .in(CONFIG_KEY, configKeys, CollectionUtils.isNotEmpty(configKeys))
                .eq(APP_ID, appId)
                .eq(CONFIG_TYPE, configType)
                .orderBy(ID, false);
        List<SystemGeneralConfigDO> list = list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 根据KEY获取租户配置
     *
     * @param key 配置key
     * @return 配置
     */
    public SystemGeneralConfigDO getTenantConfigByKey(String key) {
        return getOne(query().eq(CONFIG_KEY, key)
                .eq(CONFIG_TYPE, ConfigTypeEnum.TENANT.getCode()));
    }

    /**
     * 更新配置值（set方式，避免全量更新覆盖不必要字段）
     *
     * @param id    配置ID
     * @param value 配置值
     */
    public void updateConfigValue(Long id, String value) {
        updateChain().set(SYSTEM_CONFIG.CONFIG_VALUE, value)
                .where(SYSTEM_CONFIG.ID.eq(id))
                .update();
    }
}

