package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 16:37
 */
public interface AppVersionService {

    PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo);

    void onlineApplication(VersionOnlineReq createReqVO);

    void offlineApplication();

    void restoreApplicationVersion(Long versionId);

    void deleteApplicationVersion(Long versionId);

 }
