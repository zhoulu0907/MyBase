package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.executor;


import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.*;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.permission.SemanticQueryPermissionHelper;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DashMetaDataPageExecutor {
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticProcessLogger semanticProcessLogger;
    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
    @Resource
    private SemanticQueryPermissionHelper semanticQueryPermissionHelper;
    @Resource
    private SemanticQueryConditionBuilder semanticQueryConditionBuilder;



    public PageResult<Map<String, Object>> execute(String tableName,
                                                   Long menuId,
                                                   String traceId,
                                                   SemanticPageBodyVO body) {
        return doExecuteProcess(tableName, menuId, traceId, body);
    }

    private PageResult<Map<String, Object>> doExecuteProcess(String tableName,
                                                            Long menuId,
                                                            String traceId,
                                                            SemanticPageBodyVO body) {
        try {
            // 1) 构建 RecordDTO（包含分页排序过滤上下文）
            SemanticRecordDTO record = semanticMergeRecordAssembler.assemblePageBody(tableName, body, menuId, traceId,
                    SemanticMethodCodeEnum.GET_PAGE, SemanticDataMethodOpEnum.GET_PAGE);
            // 5) 数据权限校验：当前类 validateDataPermission
            QueryWrapper queryWrapper = new QueryWrapper();

            // 6) 构建分页查询条件并执行查询（抽象到执行器）
            queryWrapper = buildPageQueryWrapper(record, queryWrapper);
            PageResult<Map<String, Object>> result = semanticDataCrudService.queryPage(record, queryWrapper);

            return result;
        } catch (Exception e) {
            log.error("分页查询失败。tableName={}, menuId={}, traceId={}", tableName, menuId, traceId, e);
            throw e;
        }
    }

    private QueryWrapper buildPageQueryWrapper(SemanticRecordDTO recordDTO, QueryWrapper queryWrapper) {
        if (queryWrapper == null) { queryWrapper = QueryWrapper.create(); }
        List<SemanticFieldSchemaDTO> fields = recordDTO.getEntitySchema().getFields();
        var condition = recordDTO.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = recordDTO.getRecordContext().getSortBy();
        semanticQueryConditionBuilder.apply(queryWrapper, fields, condition, sortBy);
        return queryWrapper;
    }
}
