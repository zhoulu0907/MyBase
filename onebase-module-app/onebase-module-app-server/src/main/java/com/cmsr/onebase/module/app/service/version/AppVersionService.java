package com.cmsr.onebase.module.app.service.version;

import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionListRespVO;

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
