package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;

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

    void updateApplicationVersion(Long id, String versionNumber, String versionUrl);

    void deleteApplication(Long id,String name);

    Long generateId();

    List<ApplicationDO> getSimpleAppList(Integer status);

    List<ApplicationDO> getMySimpleAppListByName(String appName);
}
