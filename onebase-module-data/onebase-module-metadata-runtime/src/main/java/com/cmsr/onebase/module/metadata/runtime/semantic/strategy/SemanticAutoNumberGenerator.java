package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class SemanticAutoNumberGenerator {
    @Resource
    private AutoNumberService autoNumberService;

    //TODO: 待优化，此处应该修改到数据存储中
    public void generate(ProcessContext context) {
        List<MetadataEntityFieldDO> fields = context.getFields();
        Map<String, Object> data = context.getProcessedData();
        if (fields == null || data == null) { return; }
        for (MetadataEntityFieldDO f : fields) {
            Long fieldId = f.getId();
            String fieldName = f.getFieldName();
            if (fieldId == null || fieldName == null) { continue; }
            if (!autoNumberService.hasAutoNumber(fieldId)) { continue; }
            Object existing = data.get(fieldName);
            if (existing != null && String.valueOf(existing).length() > 0) { continue; }
            String number = autoNumberService.generateNumber(fieldId, data);
            data.put(fieldName, number);
        }
    }
}
