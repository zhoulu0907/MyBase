package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SemanticDetailExecutor {
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;
    // no uid generator needed for detail

    public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticTargetBodyVO target) {
        Map<String, Object> result = doExecuteProcess(tableName, menuId, traceId, target);
        return result;
    }

    public Map<String, Object> doExecuteProcess(String tableName, Long menuId, String traceId, SemanticTargetBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含实体校验与基本数据映射）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.GET,
                    MetadataDataMethodOpEnum.GET);

            // 2) 权限上下文初始化：当前类 initializeContext
            semanticPermissionContextLoader.loadPermissionContext(record);

            // 3) 数据完整性校验：当前类 validateDataIntegrity
            semanticDataIntegrityValidator.validate(record);

            // 4) 功能权限校验
            semanticPermissionValidator.validate(record);

            // 6) 数据查询：按主键读取主表数据
            Map<String, Object> result = semanticDataCrudService.readById(record);

            // 7) 返回值：返回详情数据 Map
            return result;
        } catch (Exception e) {
            log.error("查询详情失败。tableName={}, menuId={}, traceId={}", tableName, menuId, traceId, e);
            throw e;
        }
    }
}
