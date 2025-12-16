package com.cmsr.onebase.module.system.service.config;

import cn.hutool.extra.spring.SpringUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;


import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.database.SystemGeneralConfigDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.system.vo.config.*;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.CONFIG_NO_EXISTS;


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
    public SystemGeneralConfigVO getConfigByKey(String key) {
        SystemGeneralConfigDO config = systemGeneralConfigDataRepository.getConfigByKey(key);
        SystemGeneralConfigVO vo=    BeanUtils.toBean(config, SystemGeneralConfigVO.class);
        return vo;

    }

    @Override
    public List<SystemGeneralConfigDO> getConfigList(SystemConfigPageReqVO pageReqVO) {
        return systemGeneralConfigDataRepository.findConfigList(pageReqVO);
    }

    @Override
    public void updateStatus(Long id,Integer status) {
        SystemGeneralConfigDO configDO = systemGeneralConfigDataRepository.findById(id);
        if (null == configDO) {
            throw exception(ErrorCodeConstants.CONFIG_NO_EXISTS);
        }
        configDO.setStatus(status);
        systemGeneralConfigDataRepository.update(configDO);

    }


}