package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:37
 */
public interface ApplicationVersionService {

    List<ApplicationVersionListRespVO> listApplicationVersion(Long applicationId);


    Long createApplicationVersion(ApplicationVersionCreateReqVO applicationVersionCreateReqVO);


    void turnOnApplicationVersion(Long applicationId, String versionNumber);

    void deleteApplicationVersion(Long applicationId, String versionNumber);
}
