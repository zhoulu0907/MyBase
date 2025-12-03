package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticSortDirectionEnum;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class SemanticQueryConditionBuilder {

    public void apply(QueryWrapper qw,
                      List<SemanticFieldSchemaDTO> fields,
                      Map<String, Object> filters,
                      List<SemanticSortRuleDTO> sortBy) {
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> e : filters.entrySet()) {
                String name = e.getKey();
                Object val = e.getValue();
                if (val == null) { continue; }
                boolean exists = fields != null && fields.stream().anyMatch(f -> name.equals(f.getFieldName()));
                if (!exists) { continue; }
                if (val instanceof String) {
                    qw.where(new QueryColumn(name).like(val));
                } else {
                    qw.where(new QueryColumn(name).eq(val));
                }
            }
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            for (SemanticSortRuleDTO rule : sortBy) {
                if (rule == null || rule.getField() == null) { continue; }
                boolean asc = rule.getDirection() == null
                        || rule.getDirection() == SemanticSortDirectionEnum.ASC;
                qw.orderBy(new QueryColumn(rule.getField()), asc);
            }
        } else {
            String pk = getPrimaryKeyFieldName(fields);
            qw.orderBy(new QueryColumn(pk), false);
        }

        if (hasDeletedField(fields)) {
            qw.where(new QueryColumn("deleted").eq("0"));
        }
    }

    private String getPrimaryKeyFieldName(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) { return "id"; }
        Optional<String> idNamed = fields.stream()
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        Optional<String> firstPk = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(SemanticFieldSchemaDTO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }

    private boolean hasDeletedField(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) { return false; }
        return fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));
    }
}

