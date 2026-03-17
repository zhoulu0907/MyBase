package com.cmsr.onebase.module.system.api.config;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.service.config.SystemConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class SystemConfigApiImpl implements SystemConfigApi {

    @Resource
    private SystemConfigService systemConfigService;


    @Override
    public CommonResult<Boolean> getAppConfig(String configKey, long appId) {
        SystemGeneralConfigDO appConfig = systemConfigService.getAppConfigByKeyAndAppId(configKey, appId);
        if (appConfig != null && Boolean.FALSE.toString().equals(appConfig.getConfigValue())) {
            return success(false);
        }
        return success(true);
    }
}
