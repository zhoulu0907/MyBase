package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.semantic.dal.DraftDynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.service.DraftSemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.*;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationManager;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DRAFT_COUNT_EXCEED_LIMIT;

@Slf4j
@Component
public class DraftSemanticCreateExecutor {

    /**
     * 单用户单表草稿数量上限
     */
    private static final int MAX_DRAFT_COUNT_PER_USER_TABLE = 20;

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
    @Resource
    private DraftDynamicMetadataRepository draftDynamicMetadataRepository;

    @Resource
    private UidGenerator uidGenerator;

    public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    public Map<String, Object> doExecuteProcess(String tableName, Long menuId, String traceId, SemanticMergeBodyVO body) {
        try {
            // 0) 校验当前用户草稿数量是否超限
            validateDraftCountLimit(tableName);

            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.CREATE, SemanticDataMethodOpEnum.CREATE);

            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 3) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 4) 功能权限校验
            //semanticPermissionValidator.validate(record);

            // 5) 数据校验（RecordDTO 简化入口）
            //semanticValidationManager.validate(record);

            // 6) 数据存储：CRUDQ 服务（RecordDTO 入口）
            draftSemanticDataCrudService.create(record);

            // 7) 数据查询：通过 DataCrudService 读取主表数据
            Map<String, Object> result = draftSemanticDataCrudService.readById(record);

            // 8) 日志记录：当前类 logProcess
            semanticProcessLogger.log(record);
            return result;
        } catch (Exception e) {
            log.error("创建数据失败。tableName={}, traceId={}", tableName, traceId, e);
            throw e;
        }
    }

    /**
     * 校验当前用户在指定表的草稿数量是否超限
     *
     * @param tableName 表名
     */
    private void validateDraftCountLimit(String tableName) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return;
        }
        long draftCount = draftDynamicMetadataRepository.countDraftByUser(tableName, userId);
        if (draftCount >= MAX_DRAFT_COUNT_PER_USER_TABLE) {
            throw exception(DRAFT_COUNT_EXCEED_LIMIT, MAX_DRAFT_COUNT_PER_USER_TABLE);
        }
    }
}
