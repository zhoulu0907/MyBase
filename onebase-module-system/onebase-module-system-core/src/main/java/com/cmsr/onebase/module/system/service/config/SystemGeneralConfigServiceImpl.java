package com.cmsr.onebase.module.system.service.config;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.database.SystemGeneralConfigDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.system.enums.config.ConfigCategoryEnum;
import com.cmsr.onebase.module.system.vo.config.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 参数配置 Service 实现类
 */
@Service
@Validated
@Slf4j
@EnableTransactionManagement
public class SystemGeneralConfigServiceImpl implements SystemGeneralConfigService {

    @Resource
    private SystemGeneralConfigDataRepository systemGeneralConfigDataRepository;
    @Override
    public Long createConfig(SystemGeneralConfigSaveReqVO createReqVO) {
        // 使用正确的Bean转换方法
        SystemGeneralConfigDO systemGeneralConfigDO = BeanUtils.toBean(createReqVO, SystemGeneralConfigDO.class);
        // 插入数据并返回ID
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.insert(systemGeneralConfigDO);
        return configDO.getId();
    }

    @Override
    public void updateConfig(SystemGeneralConfigUpdateReqVO updateReqVO) {
        SystemGeneralConfigDO systemGeneralConfigDO = BeanUtils.toBean(updateReqVO, SystemGeneralConfigDO.class);
        systemGeneralConfigDataRepository.update(systemGeneralConfigDO);
    }

    @Override
    public void deleteConfig(Long id) {
        systemGeneralConfigDataRepository.deleteById(id);
    }

    @Override
    public SystemGeneralConfigDO getConfig(Long id) {
        return systemGeneralConfigDataRepository.findById(id);
    }

    @Override
    public List<SystemGeneralConfigDO> getConfigList(SystemConfigPageReqVO pageReqVO) {
        List<SystemGeneralConfigDO> configList = systemGeneralConfigDataRepository.findConfigList(pageReqVO);
        if(CollectionUtils.isNotEmpty(configList)){
            return configList;
        }

        if (StringUtils.isBlank(pageReqVO.getName())  &&  null ==pageReqVO.getStatus()) {
            List<SystemGeneralConfigDO>  globalConfigList =  systemGeneralConfigDataRepository.findGlobaConfigList();
            if (CollectionUtils.isEmpty(globalConfigList)) {
                return globalConfigList;
            }
            globalConfigList.forEach(configDO -> {
                configDO.setId(null);
                configDO.setTenantId(TenantContextHolder.getTenantId());
                configDO.setCategory(ConfigCategoryEnum.TENANT.getCode());
                systemGeneralConfigDataRepository.insert(configDO);
            });

        }
        return systemGeneralConfigDataRepository.findConfigList(pageReqVO);


    }

    @Override
    @TenantIgnore
    public void updateStatus(Long id,Integer status) {
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.findById(id);
        if (null == configDO) {
            throw exception(ErrorCodeConstants.CONFIG_NO_EXISTS);
        }

        if (CommonStatusEnum.ENABLE.getStatus().equals(status)) {
            configDO.setConfigValue(CommonStatusEnum.ENABLE.getStatus().toString());
            configDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        } else {
            configDO.setConfigValue(CommonStatusEnum.DISABLE.getStatus().toString());
            configDO.setStatus(CommonStatusEnum.DISABLE.getStatus());
        }


        // 如果配置有互斥数据，需要更新互斥数据为修改状态的反值
        if (StringUtils.isNotBlank(configDO.getExclusiveItem())  &&  CommonStatusEnum.ENABLE.getStatus().equals(status)) {
            SystemGeneralConfigSearchVO searchVO = getSystemGeneralConfigSearchVO(configDO);
            // 获取互斥数据 忽略租户条件,
            SystemGeneralConfigDO config = systemGeneralConfigDataRepository.getConfigByDiffCategory(searchVO);
            if (null != config) {
                // 判断互斥数据是否已启用
                if(CommonStatusEnum.ENABLE.getStatus().equals(config.getStatus())){
                    throw exception(ErrorCodeConstants.CONFIG_ALREADY_ENABLE,config.getName(),configDO.getName());
                }
                if (CommonStatusEnum.ENABLE.getStatus().equals(status)) {
                    config.setConfigValue(CommonStatusEnum.DISABLE.getStatus().toString());
                    config.setStatus(CommonStatusEnum.DISABLE.getStatus());
                } else {
                    config.setConfigValue(CommonStatusEnum.ENABLE.getStatus().toString());
                    config.setStatus(CommonStatusEnum.ENABLE.getStatus());
                }
                systemGeneralConfigDataRepository.update(config);
            }
        }
        systemGeneralConfigDataRepository.update(configDO);
    }

    @NotNull
    private static SystemGeneralConfigSearchVO getSystemGeneralConfigSearchVO(SystemGeneralConfigDO configDO) {
        String category= configDO.getCategory();

        SystemGeneralConfigSearchVO searchVO = new SystemGeneralConfigSearchVO();
        searchVO.setCategory(category);
        searchVO.setConfigKey(configDO.getExclusiveItem());

        if(ConfigCategoryEnum.TENANT.getCode().equals( category)){
            searchVO.setTenantId(configDO.getTenantId());
        }
        if(ConfigCategoryEnum.CORP.getCode().equals( category)){
            searchVO.setCorpId(configDO.getCorpId());
            searchVO.setTenantId(configDO.getTenantId());
        }
        return searchVO;
    }


}