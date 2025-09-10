package com.cmsr.onebase.module.metadata.api.backupmanage;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupReqDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupRespDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataRestoreReqDTO;
import com.cmsr.onebase.module.metadata.convert.backupmanage.MetadataBackupConvert;
import com.cmsr.onebase.module.metadata.service.backupmanage.MetadataBackupService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 元数据备份恢复 API 实现类
 *
 * @author matianyu
 * @date 2025-08-12
 */
@RestController
@Validated
@Slf4j
public class MetadataBackupApiImpl implements MetadataBackupApi {

    @Resource
    private MetadataBackupService metadataBackupService;

    @Override
    public CommonResult<MetadataBackupRespDTO> backupMetadata(MetadataBackupReqDTO backupReqDTO) {
        log.info("RPC 接口 - 备份元数据，应用ID: {}", backupReqDTO.getAppId());
        
        var backupReqVO = MetadataBackupConvert.INSTANCE.convert(backupReqDTO);
        var backupRespVO = metadataBackupService.backupMetadata(backupReqVO.getAppId());
        var result = MetadataBackupConvert.INSTANCE.convert(backupRespVO);
        
        log.info("RPC 接口 - 完成备份元数据，应用ID: {}", backupReqDTO.getAppId());
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<Boolean> restoreMetadata(MetadataRestoreReqDTO restoreReqDTO) {
        log.info("RPC 接口 - 恢复元数据，目标应用ID: {}", restoreReqDTO.getTargetAppId());
        
        var restoreReqVO = MetadataBackupConvert.INSTANCE.convert(restoreReqDTO);
        metadataBackupService.restoreMetadata(restoreReqVO);
        
        log.info("RPC 接口 - 完成恢复元数据，目标应用ID: {}", restoreReqDTO.getTargetAppId());
        return CommonResult.success(true);
    }

}
