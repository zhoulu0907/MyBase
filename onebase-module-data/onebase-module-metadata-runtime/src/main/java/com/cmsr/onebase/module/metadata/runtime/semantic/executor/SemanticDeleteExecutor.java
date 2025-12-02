package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticProcessLogger;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Slf4j
@Component
public class SemanticDeleteExecutor {
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticValidationManager semanticValidationManager;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
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
            // 1) 提取目标ID并构建合并请求体（便于统一装配）
            SemanticMergeBodyVO merge = new SemanticMergeBodyVO();
            Object id = body == null || body.getData() == null ? null : body.getData().get("id");
            if (id == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
            merge.set("id", id);
            
            // 2) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assemble(tableName, merge, menuId, traceId);
            record.getRecordContext().setMethodCode(SemanticMethodCodeEnum.DELETE);
            record.getRecordContext().setOperationType(MetadataDataMethodOpEnum.DELETE);
            
            // 3) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);
            
            // 4) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);
            
            // 5) 功能权限校验
            semanticPermissionValidator.validate(record);
            
            // // 6) 数据校验（RecordDTO 简化入口）
            // semanticValidationManager.validate(record);
            
            // 7) 数据删除：CRUDQ 服务（RecordDTO 入口）
            semanticDataCrudService.delete(record);
            
            // 8) 日志记录：当前类 logProcess
            semanticProcessLogger.log(record);
            return true;
        } catch (Exception e) {
            log.error("删除数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }
}
