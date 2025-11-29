package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticTemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticContextInitializer;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataFetcher;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDefaultValueProcessor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticEntityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticFieldLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticWorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticProcessLogger;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticResultFormatter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticAutoNumberGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionContextLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SemanticCreateExecutor {
    @Resource
    private SemanticEntityValidator semanticEntityValidator;
    @Resource
    private SemanticFieldLoader semanticFieldLoader;
    @Resource
    private SemanticContextInitializer semanticContextInitializer;
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticDefaultValueProcessor semanticDefaultValueProcessor;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticValidationManager semanticValidationManager;
    @Resource
    private SemanticWorkflowExecutor semanticWorkflowExecutor;
    @Resource
    private SemanticAutoNumberGenerator semanticAutoNumberGenerator;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
    @Resource
    private SemanticDataFetcher semanticDataFetcher;
    @Resource
    private SemanticResultFormatter semanticResultFormatter;
    @Resource
    private SemanticProcessLogger semanticProcessLogger;
    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;

    public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    public Map<String, Object> doExecuteProcess(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assemble(tableName, body, menuId, traceId);

            // 2) 上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 3) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 4) 功能权限校验
            semanticPermissionValidator.validate(record);
            
            // 5) 数据校验（RecordDTO 简化入口）
            semanticValidationManager.validate(record);

            // 6) 前置工作流：预留接口（当前为空实现）
            semanticWorkflowExecutor.preExecute(record);
            
            // 7) 自动编号：使用 AutoNumberService 等（在核心类中）
            // autoNumberGenerator.generate(context);

            // 8) 数据存储：CRUDQ 服务（RecordDTO 入口）
            semanticDataCrudService.execute(record);
            
            // 9) 后置工作流：预留接口（当前为空实现）
            semanticWorkflowExecutor.postExecute(record);
            // 10) 数据查询：通过 DataCrudService 读取主表数据
            Map<String, Object> fetchedData = semanticDataCrudService.readById(record);
            // 14) 结果格式化：直接格式化查询结果，无需写回上下文
            // Map<String, Object> result = resultFormatter.format(entity, fields, fetchedData, context);
            // 15) 日志记录：当前类 logProcess
            // processLogger.log(context);
            return fetchedData;
        } catch (Exception e) {
            log.error("创建数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }
}
