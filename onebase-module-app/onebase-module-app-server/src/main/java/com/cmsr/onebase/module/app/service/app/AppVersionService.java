package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.admin.app.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.VersionListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:37
 */
public interface AppVersionService {

    List<VersionListRespVO> listApplicationVersion(Long applicationId);

    void createApplicationVersion(VersionCreateReqVO createReqVO);

    void restoreApplicationVersion(Long versionId);

    void deleteApplicationVersion(Long versionId);
}
