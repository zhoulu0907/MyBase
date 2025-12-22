package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.enums.config.ConfigTypeEnum;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigSearchVO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class SystemGeneralConfigDataRepository  extends DataRepository<SystemGeneralConfigDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public SystemGeneralConfigDataRepository() {
        super(SystemGeneralConfigDO.class);
    }



    public List<SystemGeneralConfigDO> findTenantConfigList(String name,Integer status,String configType) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_TYPE, ConfigTypeEnum.TENANT.getCode());
        if (StringUtils.isNotBlank(name)) {
            configs.and(Compare.LIKE, SystemGeneralConfigDO.NAME, name);
        }
        if (null != status) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.STATUS, status);
        }
        if (null != configType) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_TYPE, configType);
        }

        // 添加排序条件，按ID降序排列
        configs.order(SystemGeneralConfigDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findAllByConfig(configs);
    }

    @TenantIgnore
    public SystemGeneralConfigDO getConfigByDiffCategory(SystemGeneralConfigSearchVO searchVO) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotBlank(searchVO.getCategory())) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_TYPE, searchVO.getCategory());
        }
        if (StringUtils.isNotBlank(searchVO.getConfigKey())) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_KEY, searchVO.getConfigKey());
        }
        if (null != searchVO.getTenantId()) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.TENANT_ID, searchVO.getTenantId());
        }
        if (null != searchVO.getCorpId()) {
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CORP_ID, searchVO.getCorpId());
        }
        return findOne(configs);
    }

    @TenantIgnore
    public List<SystemGeneralConfigDO> findGlobaConfigListByKeys(List<String> configKeys) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_TYPE, ConfigTypeEnum.GLOBAL.getCode());
        if(CollectionUtils.isNotEmpty(configKeys)){
            configs.and(Compare.IN, SystemGeneralConfigDO.CONFIG_KEY, configKeys);
        }
        // 添加排序条件，按ID降序排列
        configs.order(SystemGeneralConfigDO.ID, org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configs);
    }

    public SystemGeneralConfigDO findOneByConfigKeyAndAppId(String configKey, Long appId) {
        DefaultConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_KEY, configKey);
            configs.and(Compare.EQUAL, SystemGeneralConfigDO.APP_ID, appId);
        return findOne(configs);
    }

    public List<SystemGeneralConfigDO> findConfigListByKeysAndAppId(Set<String> configKeys, Long appId, String configType) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if(CollectionUtils.isNotEmpty(configKeys)){
            configs.and(Compare.IN, SystemGeneralConfigDO.CONFIG_KEY, configKeys);
        }
        configs.and(Compare.EQUAL, SystemGeneralConfigDO.APP_ID, appId);
        configs.and(Compare.EQUAL, SystemGeneralConfigDO.CONFIG_TYPE, configType);
        // 添加排序条件，按ID降序排列
        configs.order(SystemGeneralConfigDO.ID, org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configs);
    }
}
