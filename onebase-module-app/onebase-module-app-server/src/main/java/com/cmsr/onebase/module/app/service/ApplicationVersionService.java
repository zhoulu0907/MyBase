package com.cmsr.onebase.module.app.service;

import com.cmsr.onebase.module.app.controller.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.vo.ApplicationVersionListRespVO;

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
