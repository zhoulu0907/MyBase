package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticSortDirectionEnum;
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
                      SemanticConditionDTO condition,
                      List<SemanticSortRuleDTO> sortBy) {
        if (condition != null) {
            String name = resolveFieldName(condition, fields);
            List<Object> values = condition.getFieldValue();
            Object val = (values == null || values.isEmpty()) ? null : values.get(0);
            if (name != null && val != null) {
                boolean exists = fields != null && fields.stream().anyMatch(f -> name.equals(f.getFieldName()));
                if (exists) {
                    String op = condition.getOperator();
                    String nop = op == null ? null : op.trim().toLowerCase();
                    if (nop == null) {
                        if (val instanceof String) { qw.where(new QueryColumn(name).like(val)); }
                        else { qw.where(new QueryColumn(name).eq(val)); }
                    } else if (nop.equals("=") || nop.equals("==") || nop.equals("eq")) {
                        qw.where(new QueryColumn(name).eq(val));
                    } else if (nop.equals("!=") || nop.equals("<>") || nop.equals("ne")) {
                        qw.where(new QueryColumn(name).ne(val));
                    } else if (nop.equals(">") || nop.equals("gt")) {
                        qw.where(new QueryColumn(name).gt(val));
                    } else if (nop.equals(">=") || nop.equals("ge")) {
                        qw.where(new QueryColumn(name).ge(val));
                    } else if (nop.equals("<") || nop.equals("lt")) {
                        qw.where(new QueryColumn(name).lt(val));
                    } else if (nop.equals("<=") || nop.equals("le")) {
                        qw.where(new QueryColumn(name).le(val));
                    } else if (nop.equals("like") || nop.equals("contains")) {
                        qw.where(new QueryColumn(name).like(val));
                    } else if (nop.equals("in")) {
                        if (values != null && !values.isEmpty()) { qw.where(new QueryColumn(name).in(values)); }
                    } else if (nop.equals("not in") || nop.equals("nin")) {
                        if (values != null && !values.isEmpty()) { qw.where(new QueryColumn(name).notIn(values)); }
                    } else {
                        if (val instanceof String) { qw.where(new QueryColumn(name).like(val)); }
                        else { qw.where(new QueryColumn(name).eq(val)); }
                    }
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

    private String resolveFieldName(SemanticConditionDTO cond, List<SemanticFieldSchemaDTO> fields) {
        String name = cond.getFieldName();
        if ((name == null || name.isBlank()) && cond.getFieldUuid() != null && fields != null) {
            Optional<String> n = fields.stream()
                    .filter(f -> f != null && cond.getFieldUuid().equals(f.getFieldUuid()))
                    .map(SemanticFieldSchemaDTO::getFieldName)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (n.isPresent()) { name = n.get(); }
        }
        return name;
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
