package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.util.SemanticExecutorUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATA_METHOD_EXEC_FAIL;

@Component
public class SemanticDataIntegrityValidator {

    public void validate(SemanticRecordDTO record) {
        MetadataDataMethodOpEnum op = record.getContext().getOperationType();
        
        if (requiresId(op)) { validateId(record); }
        if (op == MetadataDataMethodOpEnum.CREATE) { validateCreateData(record); }
    }

    private boolean requiresId(MetadataDataMethodOpEnum op) {
        return op == MetadataDataMethodOpEnum.UPDATE
                || op == MetadataDataMethodOpEnum.DELETE
                || op == MetadataDataMethodOpEnum.GET;
    }

    private void validateId(SemanticRecordDTO record) {
        Long id = SemanticExecutorUtils.idOf(record);
        if (id == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS, "缺少主键id"); }
    }

    private void validateCreateData(SemanticRecordDTO record) {
        Map<String, Object> data = SemanticExecutorUtils.nameValueMapOf(record);
        if (data == null || data.isEmpty()) { throw exception(DATA_METHOD_EXEC_FAIL, "数据不能为空"); }
    }
}
