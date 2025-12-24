package com.cmsr.onebase.module.app.runtime.service.app;

import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.runtime.vo.app.AppLeastInfo;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:07
 */
public interface AppApplicationService {

    ApplicationRespVO getApplication(Long id);

    ApplicationNavigationConfigVO getApplicationNavigationConfig(Long id);

    AppLeastInfo getApplicationLeastInfo(Long id);

}
