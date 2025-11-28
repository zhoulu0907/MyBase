package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SubEntityProcessor {
    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;
    @Resource
    private TableNameQuoter tableNameQuoter;

    public void process(AnylineService<?> service, List<MetadataDataMethodSubEntityContext> subs, MetadataDataMethodOpEnum op) {
        if (subs == null || subs.isEmpty()) { return; }
        for (MetadataDataMethodSubEntityContext sub : subs) {
            Long subEntityId = sub.getEntityId();
            if (subEntityId == null || sub.getSubData() == null) { continue; }
            MetadataBusinessEntityDO subEntity = metadataBusinessEntityCoreService.getBusinessEntity(subEntityId);
            if (subEntity == null) { continue; }
            List<MetadataEntityFieldDO> subFields = metadataEntityFieldService.getEntityFieldListByEntityId(subEntityId);
            Map<Long, String> idToName = new HashMap<>();
            for (MetadataEntityFieldDO f : subFields) {
                if (f.getId() != null && f.getFieldName() != null) idToName.put(f.getId(), f.getFieldName());
            }
            String subPk = getPrimaryKeyFieldName(subFields);
            boolean subHasDeleted = subFields.stream().anyMatch(ff -> "deleted".equalsIgnoreCase(ff.getFieldName()));
            for (Map<Long,Object> rowMap : sub.getSubData()) {
                DataRow row = new DataRow();
                for (Map.Entry<Long,Object> e : rowMap.entrySet()) {
                    String name = idToName.get(e.getKey());
                    if (name != null) { row.put(name, e.getValue()); }
                }
                Object subIdVal = row.get(subPk);
                if (op == MetadataDataMethodOpEnum.CREATE) {
                    service.insert(tableNameQuoter.quote(subEntity.getTableName()), row);
                } else if (op == MetadataDataMethodOpEnum.UPDATE) {
                    if (subIdVal == null) { service.insert(tableNameQuoter.quote(subEntity.getTableName()), row); }
                    else {
                        DefaultConfigStore cs = new DefaultConfigStore();
                        cs.and(subPk, subIdVal);
                        service.update(tableNameQuoter.quote(subEntity.getTableName()), row, cs);
                    }
                } else if (op == MetadataDataMethodOpEnum.DELETE) {
                    if (subIdVal == null) { continue; }
                    DefaultConfigStore cs = new DefaultConfigStore();
                    cs.and(subPk, subIdVal);
                    if (subHasDeleted) {
                        DataRow del = new DataRow();
                        del.put("deleted", 1);
                        service.update(tableNameQuoter.quote(subEntity.getTableName()), del, cs);
                    } else {
                        service.delete(tableNameQuoter.quote(subEntity.getTableName()), cs);
                    }
                }
            }
        }
    }

    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        java.util.List<MetadataEntityFieldDO> pkCandidates = fields.stream()
                .filter(field -> com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
                .filter(field -> !com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsSystemField()))
                .toList();
        java.util.Optional<String> idNamed = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        java.util.Optional<String> firstPk = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }
}

