package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticProcessLogger;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SemanticUpdateExecutor {
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

    public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    public Map<String, Object> doExecuteProcess(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.UPDATE, MetadataDataMethodOpEnum.UPDATE);
            
            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);
            
            // 3) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);
            
            // 4) 功能权限校验
            semanticPermissionValidator.validate(record);
            
            // // 5) 数据校验（RecordDTO 简化入口）
            // semanticValidationManager.validate(record);
            
            // 6) 数据存储：CRUDQ 服务（RecordDTO 入口）
            semanticDataCrudService.update(record);
            
            // 7) 数据查询：通过 DataCrudService 读取主表数据
            Map<String, Object> result = semanticDataCrudService.readById(record);
            
            // 8) 日志记录：当前类 logProcess
            semanticProcessLogger.log(record);
            return result;
        } catch (Exception e) {
            log.error("更新数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }
}
