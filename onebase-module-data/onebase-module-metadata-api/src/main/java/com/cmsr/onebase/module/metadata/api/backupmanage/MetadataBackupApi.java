package com.cmsr.onebase.module.metadata.api.backupmanage;

import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupReqDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupRespDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataRestoreReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * RPC 服务 - 元数据备份恢复
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Tag(name = "内部调用 - 元数据备份恢复")
public interface MetadataBackupApi {

    @Operation(summary = "备份元数据", description = "根据应用ID备份所有相关的元数据信息")
    MetadataBackupRespDTO backupMetadata(@RequestBody MetadataBackupReqDTO backupReqDTO);


    @Operation(summary = "恢复元数据", description = "将备份的元数据恢复到指定应用中")
    Boolean restoreMetadata(@RequestBody MetadataRestoreReqDTO restoreReqDTO);

}
