package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationPageRespVO;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:07
 */
public interface ApplicationService {

    PageResult<ApplicationPageRespVO> getApplicationPage(ApplicationPageReqVO pageReqVO);

    Long createApplication(ApplicationCreateReqVO applicationCreateReqVO);

    void updateApplication(ApplicationCreateReqVO applicationCreateReqVO);

    void updateApplicationName(Long id, String name);

    void deleteApplication(Long id, String name);
}
