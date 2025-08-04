package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationVersionListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:37
 */
public interface VersionService {

    List<ApplicationVersionListRespVO> listApplicationVersion(Long applicationId);

    void createApplicationVersion(ApplicationVersionCreateReqVO createReqVO);

    void restoreApplicationVersion(Long versionId);

    void deleteApplicationVersion(Long versionId);
}
