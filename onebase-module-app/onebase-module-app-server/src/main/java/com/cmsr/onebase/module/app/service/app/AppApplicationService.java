package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;

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

 }
