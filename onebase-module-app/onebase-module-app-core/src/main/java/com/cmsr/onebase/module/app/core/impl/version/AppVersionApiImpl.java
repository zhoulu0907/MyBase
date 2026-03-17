package com.cmsr.onebase.module.app.core.impl.version;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.api.version.AppVersionApi;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * @Author：yuxin
 * @Date：2026/3/17 10:36
 */
@Setter
@Service
public class AppVersionApiImpl implements AppVersionApi {

    @Resource
    private AppVersionRepository appVersionRepository;


    @Override
    public Integer getAppThirdEnableStatus(Long applicationId) {
        AppVersionDO appVersionDO = ApplicationManager.withoutApplicationCondition(() -> appVersionRepository.findRuntimeByApplicationId(applicationId));
        if (appVersionDO == null) {
            return CommonStatusEnum.DISABLE.getStatus();
        }
        return appVersionDO.getAppThirdUserEnable();
    }
}
