package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ContextInitializer;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataFetcher;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataStoreExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.ValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DefaultValueProcessor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.EntityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.FieldLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.PermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.WorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ProcessLogger;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ResultFormatter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.AutoNumberGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.adapter.SemanticRequestParser;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.MergeRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.PermissionContextLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

@Slf4j
@Component
public class SemanticCreateExecutor {
    @Resource
    private EntityValidator entityValidator;
    @Resource
    private FieldLoader fieldLoader;
    @Resource
    private ContextInitializer contextInitializer;
    @Resource
    private DataIntegrityValidator dataIntegrityValidator;
    @Resource
    private DefaultValueProcessor defaultValueProcessor;
    @Resource
    private PermissionValidator permissionValidator;
    @Resource
    private ValidationManager validationManager;
    @Resource
    private WorkflowExecutor workflowExecutor;
    @Resource
    private AutoNumberGenerator autoNumberGenerator;
    @Resource
    private DataStoreExecutor dataStoreExecutor;
    @Resource
    private DataFetcher dataFetcher;
    @Resource
    private ResultFormatter resultFormatter;
    @Resource
    private ProcessLogger processLogger;
    @Resource
    private SemanticRequestParser semanticRequestParser;
    @Resource
    private com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService businessEntityCoreService;
    @Resource
    private MergeRecordAssembler mergeRecordAssembler;
    @Resource
    private PermissionContextLoader permissionContextLoader;

    public Map<String, Object> execute(String entityCode, Long menuId, String traceId, SemanticMergeBodyVO body) {
        return doExecuteProcess(entityCode, menuId, traceId, body);
    }

    public Map<String, Object> doExecuteProcess(String entityCode, Long menuId, String traceId, SemanticMergeBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            RecordDTO record = mergeRecordAssembler.assemble(entityCode, body, menuId, traceId);
            // // 2) 实体校验：当前使用 MetadataBusinessEntityCoreService.validateEntityExists
            // MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
            // if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
            // // 3) 字段装载：当前使用 MetadataEntityFieldCoreService.getEntityFieldListByEntityId
            // List<MetadataEntityFieldDO> fields = fieldLoader.load(entity.getId());
            // 4) 上下文初始化：当前类 initializeContext
            permissionContextLoader.loadPermissionContext(record);

           // 5) 数据完整性校验：当前类 validateDataIntegrity
            dataIntegrityValidator.validate(record);

            // 6) 默认值处理/形态统一：当前类 processDataAndSetDefaults
            // Map<String, Object> processedData = defaultValueProcessor.process(context);
            // context.setProcessedData(processedData);

            // 7) 功能权限校验
            permissionValidator.validate(record);
            // 8) 数据校验（RecordDTO 简化入口）
            validationManager.validate(record);
            
            // 9) 前置工作流：预留接口（当前为空实现）
            workflowExecutor.preExecute(record);
            
            // // 10) 自动编号：使用 AutoNumberService 等（在核心类中）
            // autoNumberGenerator.generate(context);

            // 11) 数据存储：当前类 storeData（包含子表处理 handleSubEntities）
            dataStoreExecutor.execute(context);
            
            // 12) 后置工作流：预留接口（当前为空实现）
            workflowExecutor.postExecute(record);
            // 13) 数据查询：当前类 getData（queryDataByIdWithService）
            dataFetcher.fetch(context);
            // 14) 结果格式化：当前类 formatResult（buildDataResponse）
            Map<String, Object> result = resultFormatter.format(context);
            // 15) 日志记录：当前类 logProcess
            processLogger.log(context);
            return result;
        } catch (Exception e) {
            log.error("创建数据失败。entityCode={}, traceId={}", entityCode, traceId, e);
            throw e;
        }
    }
}
