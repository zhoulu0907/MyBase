package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DefaultValueProcessor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.EntityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.FieldLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ContextInitializer;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.PermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.ValidationManager;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.WorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.AutoNumberGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataStoreExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.DataFetcher;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ResultFormatter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ProcessLogger;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.OperatorConditionApplier;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.RowMapper;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.ResponseBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.TableNameQuoter;
import org.anyline.data.param.ConfigStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

@Slf4j
@Component
public class SemanticDataMethodExecutor {

    @Resource
    protected DataIntegrityValidator dataIntegrityValidator;
    @Resource
    protected DefaultValueProcessor defaultValueProcessor;
    @Resource
    protected EntityValidator entityValidator;
    @Resource
    protected FieldLoader fieldLoader;
    @Resource
    protected ContextInitializer contextInitializer;
    @Resource
    protected PermissionValidator permissionValidator;
    @Resource
    protected ValidationManager validationManager;
    @Resource
    protected WorkflowExecutor workflowExecutor;
    @Resource
    protected AutoNumberGenerator autoNumberGenerator;
    @Resource
    protected DataStoreExecutor dataStoreExecutor;
    @Resource
    protected DataFetcher dataFetcher;
    @Resource
    protected ResultFormatter resultFormatter;
    @Resource
    protected ProcessLogger processLogger;
    @Resource
    protected OperatorConditionApplier operatorConditionApplier;
    @Resource
    protected SemanticPageExecutor semanticPageExecutor;
    @Resource
    protected RowMapper rowMapper;
    @Resource
    protected ResponseBuilder responseBuilder;
    @Resource
    protected TableNameQuoter tableNameQuoter;

    public Map<String, Object> executeProcess(Long entityId, Long menuId, String traceId, MetadataDataMethodOpEnum op, RecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, op, record);
    }

    public Map<String, Object> executeCreate(Long entityId, Long menuId, String traceId, RecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.CREATE, record);
    }

    public Map<String, Object> executeUpdate(Long entityId, Long menuId, String traceId, RecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.UPDATE, record);
    }

    public Boolean executeDelete(Long entityId, Long menuId, String traceId, RecordDTO record) {
        doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.DELETE, record);
        return Boolean.TRUE;
    }

    public Map<String, Object> executeDetail(Long entityId, Long menuId, String traceId, RecordDTO record) {
        return doExecuteProcess(entityId, menuId, traceId, MetadataDataMethodOpEnum.GET, record);
    }

    public PageResult<Map<String, Object>> executePage(Long entityId, Long menuId, String traceId, RecordDTO record) {
        MetadataBusinessEntityDO entity = entityValidator.validateExists(entityId);
        List<MetadataEntityFieldDO> fields = fieldLoader.load(entityId);
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
    public Map<String, Object> doExecuteProcess(Long entityId, Long menuId, String traceId, MetadataDataMethodOpEnum op, RecordDTO record) {
        try {
            // 1. 实体校验（EntityValidator）
            MetadataBusinessEntityDO entity = entityValidator.validateExists(entityId);
            // 2. 字段装载（FieldLoader）
            List<MetadataEntityFieldDO> fields = fieldLoader.load(entityId);
            // 3. 上下文初始化（ContextInitializer）
            ProcessContext context = contextInitializer.initialize(entity, fields, menuId, traceId, op, record);
            // 4. 数据完整性校验（DataIntegrityValidator）
            validateDataIntegrity(context);
            // 5. 默认值处理（DefaultValueProcessor）
            Map<String, Object> processedData = processDataAndSetDefaults(context);
            context.setProcessedData(processedData);
            // 6. 功能权限校验（PermissionValidator）
            permissionValidator.validate(record);
            // 7. 数据校验（RecordDTO 简化入口）
            validationManager.validate(record);
            // 8. 前置工作流
            workflowExecutor.preExecute(record);
            // 9. 自动编号（AutoNumberGenerator）
            autoNumberGenerator.generate(context);
            // 10. 数据存储（DataStore）
            dataStoreExecutor.execute(context);
            // 11. 后置工作流
            workflowExecutor.postExecute(record);
            // 12. 数据查询（DataFetcher）
            dataFetcher.fetch(context);
            // 13. 结果格式化（ResultFormatter）
            Map<String, Object> result = resultFormatter.format(context);
            // 14. 日志记录（ProcessLogger）
            processLogger.log(context);
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
    protected MetadataBusinessEntityDO validateEntityExists(Long entityId) { return entityValidator.validateExists(entityId); }

    /**
     * 字段装载（FieldLoader）
     * 由 MetadataEntityFieldCoreService 提供实体字段集合
     */
    protected List<MetadataEntityFieldDO> getEntityFields(Long entityId) { return fieldLoader.load(entityId); }


    /**
     * 数据完整性校验（DataIntegrityValidator）
     */
    protected void validateDataIntegrity(ProcessContext context) {
        dataIntegrityValidator.validate(context);
    }

    /**
     * 默认值处理（DefaultValueProcessor）
     */
    protected Map<String, Object> processDataAndSetDefaults(ProcessContext context) {
        return defaultValueProcessor.process(context);
    }

    /**
     * 功能权限校验（PermissionValidator）
     */
    protected void validatePermission(ProcessContext context) { permissionValidator.validate(context); }

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
    protected void generateDataNumber(ProcessContext context) { autoNumberGenerator.generate(context); }

    /**
     * 数据存储（DataStore）
     */
    protected void storeData(ProcessContext context) { dataStoreExecutor.execute(context); }

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
    protected Map<String, Object> getData(ProcessContext context) { return dataFetcher.fetch(context); }

    /**
     * 字段级权限过滤（DataFetcher 辅助）
     */
    protected Map<String, Object> filterQueryResultFields(Map<String, Object> data, ProcessContext context) { return data; }

    /**
     * 结果格式化（ResultFormatter）
     */
    protected Map<String, Object> formatResult(ProcessContext context) { return resultFormatter.format(context); }

    /**
     * 日志记录（ProcessLogger）
     */
    protected void logProcess(ProcessContext context) { processLogger.log(context); }

    /**
     * 上下文初始化（ContextInitializer）
     */
    protected ProcessContext initializeContext(MetadataBusinessEntityDO entityDO, List<MetadataEntityFieldDO> fields, Long menuId, String traceId, MetadataDataMethodOpEnum op, RecordDTO record) { return contextInitializer.initialize(entityDO, fields, menuId, traceId, op, record); }

    

    private void applyOperatorCondition(ConfigStore configs, String fieldName, String operator, Object value) { operatorConditionApplier.apply(configs, fieldName, operator, value); }

    
}
