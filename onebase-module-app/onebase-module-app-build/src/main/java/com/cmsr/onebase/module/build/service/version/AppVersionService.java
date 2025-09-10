package com.cmsr.onebase.module.build.service.version;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.build.controller.version.vo.VersionCreateReqVO;
import com.cmsr.onebase.module.build.controller.version.vo.VersionPageReqVo;
import com.cmsr.onebase.module.build.controller.version.vo.VersionPageRespVO;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 16:37
 */
public interface AppVersionService {

    PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo);

    void createApplicationVersion(VersionCreateReqVO createReqVO);

    void restoreApplicationVersion(Long versionId);

    void deleteApplicationVersion(Long versionId);
}
