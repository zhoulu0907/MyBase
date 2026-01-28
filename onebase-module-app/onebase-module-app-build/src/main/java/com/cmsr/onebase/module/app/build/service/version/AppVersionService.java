package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageReqVO;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageRespVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionImportReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    void importApplicationVersion(VersionImportReq versionImportReq);

    /**
     * 导出应用（异步）
     *
     * @param versionId 版本ID
     * @return 导出记录ID
     */
    Long exportApplicationVersion(Long versionId, Long applicationId);

    /**
     * 根据导出记录ID获取导出资源
     *
     * @param exportId 导出记录ID
     * @param request  HTTP 请求
     * @param response HTTP 响应，用于输出文件流（仅当状态为成功时）
     */
    void getExportApplicationResource(Long exportId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 查询导出状态
     *
     * @param exportId 导出记录ID
     * @return 导出状态值
     */
    Integer getExportStatus(Long exportId);

    /**
     * 分页查询导出记录
     *
     * @param pageReqVO 分页查询请求
     * @return 分页结果
     */
    PageResult<ExportPageRespVO> getExportPage(ExportPageReqVO pageReqVO);

    /**
     * 删除导出记录
     *
     * @param exportId 导出记录ID
     */
    void deleteExportRecord(Long exportId);

    /**
     * 重试导出应用
     *
     * @param exportId 导出记录ID
     * @return 导出记录ID（返回原导出记录ID）
     */
    Long retryExportApplication(Long exportId, Long applicationId);

}
