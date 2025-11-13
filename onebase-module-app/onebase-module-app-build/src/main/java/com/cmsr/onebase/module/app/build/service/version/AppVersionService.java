package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.version.VersionDO;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 16:37
 */
public interface AppVersionService {

    PageResult<VersionPageRespVO> getApplicationVersionPage(VersionPageReqVo listReqVo);

    void createApplicationVersion(VersionCreateReqVO createReqVO);

    void restoreApplicationVersion(Long versionId);

    void deleteApplicationVersion(Long versionId);

    Map<Long, VersionDO> findDevelopStatusMap(List<Long> appIds);
}
