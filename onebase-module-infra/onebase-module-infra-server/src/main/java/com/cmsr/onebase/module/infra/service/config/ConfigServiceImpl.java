package com.cmsr.onebase.module.infra.service.config;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import com.cmsr.onebase.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.cmsr.onebase.module.infra.convert.config.ConfigConvert;
import com.cmsr.onebase.module.infra.dal.dataobject.config.ConfigDO;
import com.cmsr.onebase.module.infra.enums.config.ConfigTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.*;

/**
 * 参数配置 Service 实现类
 */
@Service
@Slf4j
@Validated
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createConfig(ConfigSaveReqVO createReqVO) {
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(null, createReqVO.getKey());

        // 插入参数配置
        ConfigDO config = ConfigConvert.INSTANCE.convert(createReqVO);
        config.setType(ConfigTypeEnum.CUSTOM.getType());
        dataRepository.insert(config);
        return config.getId();
    }

    @Override
    public void updateConfig(ConfigSaveReqVO updateReqVO) {
        // 校验自己存在
        validateConfigExists(updateReqVO.getId());
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(updateReqVO.getId(), updateReqVO.getKey());

        // 更新参数配置
        ConfigDO updateObj = ConfigConvert.INSTANCE.convert(updateReqVO);
        dataRepository.update(updateObj);
    }

    @Override
    public void deleteConfig(Long id) {
        // 校验配置存在
        ConfigDO config = validateConfigExists(id);
        // 内置配置，不允许删除
        if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
            throw exception(CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
        }
        // 删除
        dataRepository.deleteById(ConfigDO.class, id);
    }

    @Override
    public ConfigDO getConfig(Long id) {
        return dataRepository.findById(ConfigDO.class, id);
    }

    @Override
    public ConfigDO getConfigByKey(String key) {
        return dataRepository.findOne(ConfigDO.class, new DefaultConfigStore().eq(ConfigDO.CONFIG_KEY, key));
    }

    @Override
    public PageResult<ConfigDO> getConfigPage(ConfigPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.like(ConfigDO.NAME, pageReqVO.getName())
                .like(ConfigDO.CONFIG_KEY, pageReqVO.getKey())
                .eq(ConfigDO.TYPE, pageReqVO.getType());
        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.ge(ConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            configStore.le(ConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }
        return dataRepository.findPageWithConditions(ConfigDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @VisibleForTesting
    public ConfigDO validateConfigExists(Long id) {
        if (id == null) {
            return null;
        }
        ConfigDO config = dataRepository.findById(ConfigDO.class, id);
        if (config == null) {
            throw exception(CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @VisibleForTesting
    public void validateConfigKeyUnique(Long id, String key) {
        ConfigDO config = dataRepository.findOne(ConfigDO.class, new DefaultConfigStore().eq(ConfigDO.CONFIG_KEY, key));
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的参数配置
        if (id == null) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
        if (!config.getId().equals(id)) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
    }

}