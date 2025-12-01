package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.framework.uid.UidGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SemanticCreateExecutor {
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
    private UidGenerator uidGenerator;

    public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    public Map<String, Object> doExecuteProcess(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assemble(tableName, body, menuId, traceId);

            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 3) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 4) 功能权限校验
            semanticPermissionValidator.validate(record);
            
            // 5) 数据校验（RecordDTO 简化入口）
            semanticValidationManager.validate(record);

            // // 6) 前置工作流：预留接口
            // semanticWorkflowExecutor.preExecute(record);

            // 8) 数据存储：CRUDQ 服务（RecordDTO 入口）
            semanticDataCrudService.create(record);;
            
            // // 9) 后置工作流：预留接口
            // semanticWorkflowExecutor.postExecute(record);
            // 10) 数据查询：通过 DataCrudService 读取主表数据
            Map<String, Object> result = semanticDataCrudService.readById(record);
            // 14) 结果格式化：直接格式化查询结果，无需写回上下文
            // Map<String, Object> result = resultFormatter.format(entity, fields, fetchedData, context);
            // 15) 日志记录：当前类 logProcess
            // processLogger.log(context);
            return result;
        } catch (Exception e) {
            log.error("创建数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }
}
