package com.cmsr.onebase.module.app.api.version;

import com.cmsr.onebase.module.app.api.version.dto.AppNavigationDTO;
import com.cmsr.onebase.module.app.api.version.dto.AppVersionDTO;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppVersionApi {

    AppVersionDTO getAppVersionRuntime(Long appId);


    AppNavigationDTO getAppNavigationRuntime(Long applicationId);
}
