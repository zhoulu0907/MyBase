package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.permission.builder.PermissionContextBuilder;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class SemanticContextInitializer {
    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    @Resource
    private PermissionContextBuilder permissionContextBuilder;

    public ProcessContext initialize(MetadataBusinessEntityDO entityDO, List<MetadataEntityFieldDO> fields, Long menuId, String traceId, com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum op, SemanticRecordDTO record) {
        ProcessContext processContext = new ProcessContext();
        // String ctxTraceId = (record != null && record.getContext() != null && record.getContext().getTraceId() != null)
        //         ? record.getContext().getTraceId() : traceId;
        // processContext.setTraceId(ctxTraceId == null ? UUID.randomUUID().toString() : ctxTraceId);
        // processContext.setEntity(entityDO);
        // processContext.setFields(fields);
        // com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum operationType =
        //         (record != null && record.getContext() != null && record.getContext().getOperationType() != null)
        //                 ? record.getContext().getOperationType() : op;
        // processContext.setOperationType(operationType);
        // processContext.setEntityId(entityDO.getId());
        // processContext.setData(SemanticExecutorUtils.nameValueMapOf(record));
        // processContext.setMethodCode(SemanticExecutorUtils.methodCodeOf(record));
        // Object dataId = (record != null && record.getContext() != null && record.getContext().getId() != null)
        //         ? record.getContext().getId() : SemanticExecutorUtils.idOf(record);
        // processContext.setId(dataId);
        // Long ctxMenuId = (record != null && record.getContext() != null && record.getContext().getMenuId() != null)
        //         ? record.getContext().getMenuId() : menuId;
        // processContext.setMenuId(ctxMenuId);
        // RuntimeLoginUser loginUser = RTSecurityContext.getLoginUser();
        // if (loginUser != null) {
        //     LoginUserCtx loginUserCtx = new LoginUserCtx();
        //     loginUserCtx.setUserId(loginUser.getId());
        //     loginUserCtx.setApplicationId(loginUser.getApplicationId());
        //     processContext.setLoginUserCtx(loginUserCtx);
        //     if (record != null && record.getContext() != null) {
        //         record.getContext().setLoginUserCtx(loginUserCtx);
        //         record.getContext().setUserId(loginUser.getId());
        //         record.getContext().setApplicationId(loginUser.getApplicationId());
        //     }
        // }
        // if (ctxMenuId != null) {
        //     MetadataPermissionContext pc = permissionContextBuilder.buildPermissionContext(processContext.getLoginUserCtx(), ctxMenuId, entityDO.getId());
        //     processContext.setMetadataPermissionContext(pc);
        //     if (record != null && record.getContext() != null) {
        //         record.getContext().setPermissionContext(pc);
        //     }
        // }
        // MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entityDO.getDatasourceId());
        // if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
        // processContext.setTemporaryService(temporaryService);
        return processContext;
    }

    
}
