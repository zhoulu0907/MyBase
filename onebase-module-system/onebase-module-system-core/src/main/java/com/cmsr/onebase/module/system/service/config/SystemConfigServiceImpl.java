package com.cmsr.onebase.module.system.service.config;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.database.SystemGeneralConfigDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.system.enums.config.ConfigTypeEnum;
import com.cmsr.onebase.module.system.enums.config.SystemConfigKeyEnum;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.config.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 参数配置 Service 实现类
 */
@Service
@Validated
@Slf4j
@EnableTransactionManagement
public class SystemConfigServiceImpl implements SystemConfigService {

    @Resource
    private SystemGeneralConfigDataRepository systemGeneralConfigDataRepository;
    @Resource
    private CorpService                       corpService;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Override
    public Long createConfig(SystemGeneralConfigSaveReqVO createReqVO) {
        // 使用正确的Bean转换方法
        SystemGeneralConfigDO systemGeneralConfigDO = BeanUtils.toBean(createReqVO, SystemGeneralConfigDO.class);
        // 插入数据并返回ID
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.insert(systemGeneralConfigDO);
        return configDO.getId();
    }

    public SystemGeneralConfigDO get(SystemGeneralConfigDO configDO, String key) {

        return configDO;
    }


    @Override
    public void updateConfig(SystemGeneralConfigUpdateReqVO updateReqVO) {
        SystemGeneralConfigDO systemGeneralConfigDO = BeanUtils.toBean(updateReqVO, SystemGeneralConfigDO.class);
        // 判断数据库是否存在三个配置项
        if (SystemConfigKeyEnum.appThirdUserEnable.getKey().equals(updateReqVO.getConfigKey()) ||
                SystemConfigKeyEnum.appThirdUserForgetPwdShow.getKey().equals(updateReqVO.getConfigKey()) ||
                SystemConfigKeyEnum.appThirdUserRegisterShow.getKey().equals(updateReqVO.getConfigKey())) {

            // 先判断 key，appid 对于的数是否存在，
            SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.findOneByConfigKeyAndAppId(updateReqVO.getConfigKey(), updateReqVO.getAppId());
            if (null == configDO) {
                SystemGeneralConfigDO insertConfigDO = new SystemGeneralConfigDO();
                insertConfigDO.setId(null);
                insertConfigDO.setAppId(updateReqVO.getAppId());
                insertConfigDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
                insertConfigDO.setConfigType(ConfigTypeEnum.APP.getCode());
                insertConfigDO.setConfigKey(updateReqVO.getConfigKey());
                insertConfigDO.setConfigValue(updateReqVO.getConfigValue());
                insertConfigDO.setName(SystemConfigKeyEnum.getByKey(updateReqVO.getConfigKey()).getName());
                systemGeneralConfigDataRepository.insert(insertConfigDO);
            } else {
                configDO.setConfigValue(updateReqVO.getConfigValue());
                systemGeneralConfigDataRepository.update(configDO);

            }
        } else {
            systemGeneralConfigDataRepository.update(systemGeneralConfigDO);
        }

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
    public List<SystemGeneralConfigDO> getTenantConfigList(SystemConfigReqVO configReqVO) {
        List<SystemGeneralConfigDO> configList = systemGeneralConfigDataRepository.findTenantConfigList(configReqVO.getName(), configReqVO.getStatus(), configReqVO.getConfigType());
        if (CollectionUtils.isNotEmpty(configList)) {
            return configList;
        }

        if (StringUtils.isBlank(configReqVO.getName()) && null == configReqVO.getStatus()) {
            List<SystemGeneralConfigDO> globalConfigList = systemGeneralConfigDataRepository.findGlobaConfigListByKeys(Arrays.asList(SystemConfigKeyEnum.ARRAYS));
            if (CollectionUtils.isEmpty(globalConfigList)) {
                return new ArrayList<>();
            }
            globalConfigList.forEach(configDO -> {
                if (ConfigTypeEnum.GLOBAL.getCode().equals(configDO.getConfigType())) {
                    configDO.setId(null);
                    configDO.setTenantId(TenantContextHolder.getTenantId());
                    configDO.setConfigType(ConfigTypeEnum.TENANT.getCode());
                    systemGeneralConfigDataRepository.insert(configDO);
                }
            });

        }
        return systemGeneralConfigDataRepository.findTenantConfigList(configReqVO.getName(), configReqVO.getStatus(), configReqVO.getConfigType());


    }

    @Override
    public SystemGeneralConfigDO getTenantConfigByKey(String key) {
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.getTenantConfigByKey(key);
        return configDO;
    }

    private void checkSaasExitsCorpOrApp() {
        // 如果是Saas模式,并且当前租户已存在企业，则不允许禁用
        List<CorpDO> corpList = corpService.getAllCorpList();
        if (CollectionUtils.isNotEmpty(corpList)) {
            throw exception(ErrorCodeConstants.CONFIG_SAAS_CORP_EXISTS);
        }
        // 如果是Saas模式,并且 当前租户已存在SAAS应用，存在则不可禁用
        List<ApplicationDTO> appList = appApplicationApi.findAppApplicationByAppName("");
        // appList 是否模式字段是否有saas应用
        boolean hasSaasApp = appList.stream()
                .anyMatch(app -> CommonPublishModelEnum.SaaSModel.getValue().equalsIgnoreCase(app.getPublishModel()));
        if (hasSaasApp) {
            throw exception(ErrorCodeConstants.CONFIG_SAAS_APP_EXISTS);
        }
    }


    @Override
    public void updateStatus(Long id, Integer status) {
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.findById(id);
        if (null == configDO) {
            throw exception(ErrorCodeConstants.CONFIG_NO_EXISTS);
        }

        if (CommonStatusEnum.ENABLE.getStatus().equals(status)) {
            configDO.setConfigValue(CommonStatusEnum.ENABLE.getStatus().toString());
            configDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        } else {
            // 判断配置项是否为saas配置项
            if (SystemConfigKeyEnum.SaasModeConfig.getKey().equals(configDO.getConfigKey())) {
                checkSaasExitsCorpOrApp();
            }

            configDO.setConfigValue(CommonStatusEnum.DISABLE.getStatus().toString());
            configDO.setStatus(CommonStatusEnum.DISABLE.getStatus());
        }


        // 如果配置有互斥数据，需要更新互斥数据为修改状态的反值
        if (StringUtils.isNotBlank(configDO.getExclusiveItem()) && CommonStatusEnum.ENABLE.getStatus().equals(status)) {
            SystemGeneralConfigSearchVO searchVO = getSystemGeneralConfigSearchVO(configDO);
            // 获取互斥数据 忽略租户条件,
            SystemGeneralConfigDO config = systemGeneralConfigDataRepository.getConfigByDiffCategory(searchVO);
            if (null != config) {
                // 判断互斥数据是否已启用
                if (CommonStatusEnum.ENABLE.getStatus().equals(config.getStatus())) {
                    throw exception(ErrorCodeConstants.CONFIG_ALREADY_ENABLE, config.getName(), configDO.getName());
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
        String category = configDO.getConfigType();

        SystemGeneralConfigSearchVO searchVO = new SystemGeneralConfigSearchVO();
        searchVO.setCategory(category);
        searchVO.setConfigKey(configDO.getExclusiveItem());

        if (ConfigTypeEnum.TENANT.getCode().equals(category)) {
            searchVO.setTenantId(configDO.getTenantId());
        }
        if (ConfigTypeEnum.CORP.getCode().equals(category)) {
            searchVO.setCorpId(configDO.getCorpId());
            searchVO.setTenantId(configDO.getTenantId());
        }
        return searchVO;
    }

    @Override
    public List<SystemGeneralConfigDO> getTenantConfigListByKeysAndAppId(SystemConfigSearchReqVO searchVO) {
        Set<String> configKeys = searchVO.getConfigKeys();
        Long appId = searchVO.getAppId();
        String category = searchVO.getConfigType();

        if (CollectionUtils.isEmpty(configKeys)) {
            return new ArrayList<>();
        }
        List<SystemGeneralConfigDO> configDOList = systemGeneralConfigDataRepository.findConfigListByKeysAndAppId(configKeys, appId, category);
        if (CollectionUtils.isNotEmpty(configDOList)) {
            return configDOList;
        }

        configKeys.forEach(configKey -> {
            if (SystemConfigKeyEnum.appThirdUserEnable.getKey().equals(configKey) ||
                    SystemConfigKeyEnum.appThirdUserForgetPwdShow.getKey().equals(configKey) ||
                    SystemConfigKeyEnum.appThirdUserRegisterShow.getKey().equals(configKey)) {
                SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.findOneByConfigKeyAndAppId(configKey, appId);
                if (null == configDO) {
                    SystemGeneralConfigDO insertConfigDO = new SystemGeneralConfigDO();
                    insertConfigDO.setId(null);
                    insertConfigDO.setConfigType(ConfigTypeEnum.APP.getCode());
                    insertConfigDO.setAppId(appId);
                    insertConfigDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
                    insertConfigDO.setConfigKey(configKey);
                    if (SystemConfigKeyEnum.appThirdUserEnable.getKey().equals(configKey)) {
                        insertConfigDO.setName(SystemConfigKeyEnum.appThirdUserEnable.getName());
                        insertConfigDO.setConfigValue(SystemConfigKeyEnum.appThirdUserEnable_DefaultValue);
                    }
                    if (SystemConfigKeyEnum.appThirdUserForgetPwdShow.getKey().equals(configKey)) {
                        insertConfigDO.setName(SystemConfigKeyEnum.appThirdUserForgetPwdShow.getName());
                        insertConfigDO.setConfigValue(SystemConfigKeyEnum.appThirdUserForgetPwdShow_DefaultValue);
                    }
                    if (SystemConfigKeyEnum.appThirdUserRegisterShow.getKey().equals(configKey)) {
                        insertConfigDO.setName(SystemConfigKeyEnum.appThirdUserRegisterShow.getName());
                        insertConfigDO.setConfigValue(SystemConfigKeyEnum.appThirdUserRegisterShow_DefaultValue);
                    }
                    systemGeneralConfigDataRepository.insert(insertConfigDO);
                }
            }
        });

        return systemGeneralConfigDataRepository.findConfigListByKeysAndAppId(configKeys, appId, category);

    }

}