package com.cmsr.onebase.module.metadata.build.service.backupmanage;

import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataBackupRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataRestoreReqVO;

/**
 * 元数据备份恢复 Service 接口
 *
 * @author matianyu
 * @date 2025-08-12
 */
public interface MetadataBackupBuildService {

    /**
     * 根据应用ID备份元数据
     *
     * @param appId 应用ID
     * @return 备份的元数据JSON
     */
    MetadataBackupRespVO backupMetadata(Long appId);

    /**
     * 根据应用ID恢复元数据
     *
     * @param restoreReqVO 恢复请求参数
     */
    void restoreMetadata(MetadataRestoreReqVO restoreReqVO);

}
