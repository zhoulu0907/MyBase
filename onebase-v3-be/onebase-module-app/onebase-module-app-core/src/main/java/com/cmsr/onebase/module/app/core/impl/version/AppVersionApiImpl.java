package com.cmsr.onebase.module.app.core.impl.version;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.version.AppVersionApi;
import com.cmsr.onebase.module.app.api.version.dto.AppNavigationDTO;
import com.cmsr.onebase.module.app.api.version.dto.AppVersionDTO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppNavigationRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
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

    @Resource
    private AppNavigationRepository appNavigationRepository;


    @Override
    public AppVersionDTO getAppVersionRuntime(Long applicationId) {
        AppVersionDO appVersionDO = ApplicationManager.withoutApplicationCondition(() -> appVersionRepository.findRuntimeByApplicationId(applicationId));
        if (appVersionDO != null) {
            return BeanUtils.toBean(appVersionDO, AppVersionDTO.class);
        }
        return null;
    }


    @Override
    public AppNavigationDTO getAppNavigationRuntime(Long applicationId) {
        AppNavigationDO AppNavigationDO = ApplicationManager.withoutApplicationCondition(
                () -> appNavigationRepository.findByApplicationIdAndVersionTag(applicationId, VersionTagEnum.RUNTIME.getValue()));
        if (AppNavigationDO != null) {
            return BeanUtils.toBean(AppNavigationDO, AppNavigationDTO.class);
        }
        return null;
    }
}
