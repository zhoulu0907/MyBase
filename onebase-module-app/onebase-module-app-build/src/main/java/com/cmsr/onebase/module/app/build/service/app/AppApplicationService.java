package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationNavigationConfigVO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:07
 */
public interface AppApplicationService {

    PageResult<ApplicationRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO);

    ApplicationCreateRespVO createApplication(ApplicationCreateReqVO applicationCreateReqVO);

    ApplicationRespVO getApplication(Long id);

    void updateApplication(ApplicationCreateReqVO applicationCreateReqVO);

    void updateApplicationName(Long id, String name);

    void deleteApplication(Long id, String name);

    Long generateId();

    List<AppApplicationDO> getSimpleAppList(Integer status);

    List<AppApplicationDO> getMySimpleAppListByName(String appName);

    ApplicationNavigationConfigVO getApplicationNavigationConfig(Long id);

    void updateApplicationNavigationConfig(ApplicationNavigationConfigVO updateReqVO);

}
