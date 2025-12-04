package com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataBackupReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataBackupRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataRestoreReqVO;
import com.cmsr.onebase.module.metadata.build.service.backupmanage.MetadataBackupBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台 - 元数据备份恢复
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Tag(name = "管理后台 - 元数据备份恢复")
@RestController
@RequestMapping("/metadata/backup")
@Validated
@Slf4j
public class MetadataBackupController {

    @Resource
    private MetadataBackupBuildService metadataBackupBuildService;

    @PostMapping("/backup")
    @Operation(summary = "备份元数据", description = "根据应用ID备份所有相关的元数据信息")
    public CommonResult<MetadataBackupRespVO> backupMetadata(@Valid @RequestBody MetadataBackupReqVO backupReqVO) {
        Long applicationId = ApplicationManager.getApplicationId();
        log.info("收到备份元数据请求，应用ID: {}", applicationId);

        MetadataBackupRespVO result = metadataBackupBuildService.backupMetadata(applicationId);

        log.info("完成备份元数据，应用ID: {}", applicationId);
        return CommonResult.success(result);
    }

    @PostMapping("/restore")
    @Operation(summary = "恢复元数据", description = "将备份的元数据恢复到指定应用中")
    public CommonResult<Boolean> restoreMetadata(@Valid @RequestBody MetadataRestoreReqVO restoreReqVO) {
        Long targetApplicationId = ApplicationManager.getApplicationId();
        log.info("收到恢复元数据请求，目标应用ID: {}", targetApplicationId);

        restoreReqVO.setTargetApplicationId(targetApplicationId);
        metadataBackupBuildService.restoreMetadata(restoreReqVO);

        log.info("完成恢复元数据，目标应用ID: {}", targetApplicationId);
        return CommonResult.success(true);
    }

}
