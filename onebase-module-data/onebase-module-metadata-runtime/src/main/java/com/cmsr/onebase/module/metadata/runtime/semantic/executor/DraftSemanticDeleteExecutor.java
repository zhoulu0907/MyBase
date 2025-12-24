package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.service.DraftSemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.*;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DraftSemanticDeleteExecutor {
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticValidationManager semanticValidationManager;
    @Resource
    private DraftSemanticDataCrudService draftSemanticDataCrudService;
    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;
    @Resource
    private SemanticProcessLogger semanticProcessLogger;

    public Boolean execute(String tableName, Long menuId, String traceId, SemanticTargetBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    public Boolean doExecuteProcess(String tableName, Long menuId, String traceId, SemanticTargetBodyVO body) {
        try {

            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.DELETE,
                    SemanticDataMethodOpEnum.DELETE);

            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 4) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 5) 功能权限校验
            semanticPermissionValidator.validate(record);

            // // 6) 数据校验（RecordDTO 简化入口）
            // semanticValidationManager.validate(record);

            // 7) 数据删除：CRUDQ 服务（RecordDTO 入口）
            draftSemanticDataCrudService.delete(record);

            // 8) 日志记录：当前类 logProcess
            semanticProcessLogger.log(record);
            return true;
        } catch (Exception e) {
            log.error("删除数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }

    public boolean deleteByTable(String tableName, Long menuId,SemanticTargetBodyVO body, String traceId) {
        try {

           // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.DELETE,
                    SemanticDataMethodOpEnum.DELETE);

            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 4) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 5) 功能权限校验
            semanticPermissionValidator.validate(record);

            // // 6) 数据校验（RecordDTO 简化入口）
            // semanticValidationManager.validate(record);

            // 7) 数据删除：CRUDQ 服务（RecordDTO 入口）
            draftSemanticDataCrudService.deleteByTable(record);

            // 8) 日志记录：当前类 logProcess
            semanticProcessLogger.log(record);
            return true;
        } catch (Exception e) {
            log.error("删除数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }

    }
}
