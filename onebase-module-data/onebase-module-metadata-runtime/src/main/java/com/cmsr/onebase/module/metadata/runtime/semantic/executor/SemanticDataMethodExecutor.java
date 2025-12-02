package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDefaultValueProcessor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticEntityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticFieldLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticContextInitializer;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticWorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticAutoNumberGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataFetcher;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticResultFormatter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticProcessLogger;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticOperatorConditionApplier;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticRowMapper;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticResponseBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import org.anyline.data.param.ConfigStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

@Slf4j
@Component
public class SemanticDataMethodExecutor {

    @Resource
    protected SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    protected SemanticDefaultValueProcessor semanticDefaultValueProcessor;
    @Resource
    protected SemanticEntityValidator semanticEntityValidator;
    @Resource
    protected SemanticFieldLoader semanticFieldLoader;
    @Resource
    protected SemanticContextInitializer semanticContextInitializer;
    @Resource
    protected SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    protected SemanticValidationManager semanticValidationManager;
    @Resource
    protected SemanticWorkflowExecutor semanticWorkflowExecutor;
    @Resource
    protected SemanticAutoNumberGenerator semanticAutoNumberGenerator;
    @Resource
    protected SemanticDataCrudService semanticDataCrudService;
    @Resource
    protected SemanticDataFetcher semanticDataFetcher;
    @Resource
    protected SemanticResultFormatter semanticResultFormatter;
    @Resource
    protected SemanticProcessLogger semanticProcessLogger;
    @Resource
    protected SemanticOperatorConditionApplier semanticOperatorConditionApplier;
    @Resource
    protected SemanticPageExecutor semanticPageExecutor;
    @Resource
    protected SemanticRowMapper semanticRowMapper;
    @Resource
    protected SemanticResponseBuilder semanticResponseBuilder;
    @Resource
    protected SemanticTableNameQuoter semanticTableNameQuoter;

    public Map<String, Object> executeProcess(Long entityId, Long menuId, String traceId, MetadataDataMethodOpEnum op, SemanticRecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, op, record);
    }

    public Map<String, Object> executeCreate(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.CREATE, record);
    }

    public Map<String, Object> executeUpdate(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.UPDATE, record);
    }

    public Boolean executeDelete(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.DELETE, record);
        return Boolean.TRUE;
    }

    public Map<String, Object> executeDetail(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.GET, record);
    }

    public PageResult<Map<String, Object>> executePage(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        MetadataBusinessEntityDO entity = semanticEntityValidator.validateExists(entityId);
        List<MetadataEntityFieldDO> fields = semanticFieldLoader.load(entityId);
        return semanticPageExecutor.execute(entity, fields, menuId, record);
    }

    /**
     * 统一流程步骤规划（对应 doExecuteProcess 的每一步）
     *
     * 1) 实体校验：当前使用 MetadataBusinessEntityCoreService.validateEntityExists
     * 2) 字段装载：当前使用 MetadataEntityFieldCoreService.getEntityFieldListByEntityId
     * 3) 上下文初始化：当前类 initializeContext=-
     * 4) 数据完整性校验：当前类 validateDataIntegrity
     * 5) 默认值处理/形态统一：当前类 processDataAndSetDefaults
     * 6) 功能权限校验：使用 PermissionManager.checkPermission
     * 7) 数据校验（类型/必填/唯一等）：使用 ValidationManager.validateEntity（在核心类中）
     * 8) 前置工作流：预留接口（当前为空实现）
     * 9) 自动编号：使用 AutoNumberService 等（在核心类中）
     * 10) 数据存储：当前类 storeData（包含子表处理 handleSubEntities）
     * 11) 后置工作流：预留接口（当前为空实现）
     * 12) 数据查询：当前类 getData（queryDataByIdWithService）
     * 13) 结果格式化：当前类 formatResult（buildDataResponse）
     * 14) 日志记录：当前类 logProcess
     */
    public Map<String, Object> doExecuteProcess(Long entityId, Long menuId, String traceId, MetadataDataMethodOpEnum op, SemanticRecordDTO record) {
        try {
            // 1. 实体校验（EntityValidator）
            MetadataBusinessEntityDO entity = semanticEntityValidator.validateExists(entityId);
            // 2. 字段装载（FieldLoader）
            List<MetadataEntityFieldDO> fields = semanticFieldLoader.load(entityId);
            // 3. 上下文初始化（ContextInitializer）
            ProcessContext context = semanticContextInitializer.initialize(entity, fields, menuId, traceId, op, record);
            // 4. 数据完整性校验（DataIntegrityValidator）
            validateDataIntegrity(context);
            // 5. 默认值处理（DefaultValueProcessor）
            Map<String, Object> processedData = processDataAndSetDefaults(context);
            context.setProcessedData(processedData);
            if (context.getId() == null) {
                String pkField = fields.stream()
                        .filter(f -> Integer.valueOf(1).equals(f.getIsPrimaryKey()))
                        .map(MetadataEntityFieldDO::getFieldName)
                        .filter(java.util.Objects::nonNull)
                        .findFirst()
                        .orElse("id");
                context.setId(processedData.get(pkField));
            }
            // 6. 功能权限校验（PermissionValidator）
            semanticPermissionValidator.validate(record);
            // 7. 数据校验（RecordDTO 简化入口）
            semanticValidationManager.validate(record);
            // 8. 前置工作流
            semanticWorkflowExecutor.preExecute(record);
            // 9. 自动编号（AutoNumberGenerator）
            semanticAutoNumberGenerator.generate(context);
            // 10. 数据存储（CRUDQ，RecordDTO 入口）
            semanticDataCrudService.execute(record);
            // 11. 后置工作流
            semanticWorkflowExecutor.postExecute(record);
            // 12. 数据查询（DataFetcher）
            semanticDataFetcher.fetch(context);
            // 13. 结果格式化（ResultFormatter）
            Map<String, Object> result = semanticResultFormatter.format(context);
            // 14. 日志记录（ProcessLogger）
            // semanticProcessLogger.log(context);
            return result;
        } catch (Exception e) {
            log.error("执行元数据系统方法失败。entityId={}, op={}, traceId={}",
                    entityId, op, traceId, e);
            throw exception(DATA_METHOD_EXEC_FAIL, e.getMessage());
        }
    }

    

    /**
     * 实体校验（EntityValidator）
     * 由 MetadataBusinessEntityCoreService 提供实体存在性校验
     */
    protected MetadataBusinessEntityDO validateEntityExists(Long entityId) { return semanticEntityValidator.validateExists(entityId); }

    /**
     * 字段装载（FieldLoader）
     * 由 MetadataEntityFieldCoreService 提供实体字段集合
     */
    protected List<MetadataEntityFieldDO> getEntityFields(Long entityId) { return semanticFieldLoader.load(entityId); }


    /**
     * 数据完整性校验（DataIntegrityValidator）
     */
    protected void validateDataIntegrity(ProcessContext context) {
        // dataIntegrityValidator.validate(context);
    }

    /**
     * 默认值处理（DefaultValueProcessor）
     */
    protected Map<String, Object> processDataAndSetDefaults(ProcessContext context) {
        return semanticDefaultValueProcessor.process(context);
    }

    /**
     * 功能权限校验（PermissionValidator）
     */
    protected void validatePermission(ProcessContext context) {
    }

    /**
     * 数据校验（DataValidator）
     */
    protected void validateData(ProcessContext context) { /* deprecated in favor of RecordDTO */ }

    /**
     * 前置工作流（PreWorkflowExecutor）
     */
    protected void executePreWorkflow(ProcessContext context) { /* deprecated in favor of RecordDTO */ }

    /**
     * 自动编号（AutoNumberGenerator）
     */
    protected void generateDataNumber(ProcessContext context) { semanticAutoNumberGenerator.generate(context); }

    /**
     * 数据存储（DataStore）
     */
    protected void storeData(ProcessContext context) { /* deprecated in favor of RecordDTO */ }

    /**
     * 子表处理（DataStore 扩展）
     */
    protected void handleSubEntities(ProcessContext context) { /* delegated to DataStoreExecutor */ }

    /**
     * 后置工作流（PostWorkflowExecutor）
     */
    protected void executePostWorkflow(ProcessContext context) { /* deprecated in favor of RecordDTO */ }

    /**
     * 数据查询（DataFetcher）
     */
    protected Map<String, Object> getData(ProcessContext context) { return semanticDataFetcher.fetch(context); }

    /**
     * 字段级权限过滤（DataFetcher 辅助）
     */
    protected Map<String, Object> filterQueryResultFields(Map<String, Object> data, ProcessContext context) { return data; }

    /**
     * 结果格式化（ResultFormatter）
     */
    protected Map<String, Object> formatResult(ProcessContext context) { return semanticResultFormatter.format(context); }

    /**
     * 日志记录（ProcessLogger）
     */
    // protected void logProcess(ProcessContext context) { semanticProcessLogger.log(context); }

    /**
     * 上下文初始化（ContextInitializer）
     */
    protected ProcessContext initializeContext(MetadataBusinessEntityDO entityDO, List<MetadataEntityFieldDO> fields, Long menuId, String traceId, MetadataDataMethodOpEnum op, SemanticRecordDTO record) { return semanticContextInitializer.initialize(entityDO, fields, menuId, traceId, op, record); }

    

    private void applyOperatorCondition(ConfigStore configs, String fieldName, String operator, Object value) { semanticOperatorConditionApplier.apply(configs, fieldName, operator, value); }

    
}
