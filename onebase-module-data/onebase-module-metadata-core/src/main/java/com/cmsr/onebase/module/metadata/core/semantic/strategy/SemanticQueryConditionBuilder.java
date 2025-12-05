package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticSortDirectionEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class SemanticQueryConditionBuilder {

    public void apply(QueryWrapper qw,
                      List<SemanticFieldSchemaDTO> fields,
                      SemanticConditionDTO condition,
                      List<SemanticSortRuleDTO> sortBy) {
        if (condition != null) {
            QueryCondition rootCond = buildQueryCondition(condition, fields);
            if (rootCond != null) { qw.where(rootCond); }
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

    private QueryCondition buildQueryCondition(SemanticConditionDTO cond, List<SemanticFieldSchemaDTO> fields) {
        if (cond == null) { return null; }
        List<SemanticConditionDTO> children = cond.getChildren();
        boolean markedGroup = cond.getNodeType() == SemanticConditionNodeTypeEnum.GROUP;
        if (markedGroup || (children != null && !children.isEmpty())) {
            QueryCondition group = null;
            boolean and = cond.getCombinator() == null || cond.getCombinator() == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticCombinatorEnum.AND;
            for (SemanticConditionDTO child : children == null ? Collections.<SemanticConditionDTO>emptyList() : children) {
                QueryCondition childCond = buildQueryCondition(child, fields);
                if (childCond == null) { continue; }
                if (group == null) { group = childCond; }
                else { group = and ? group.and(childCond) : group.or(childCond); }
            }
            return group;
        }

        String name = resolveFieldName(cond, fields);
        List<Object> values = cond.getFieldValue();
        Object val = (values == null || values.isEmpty()) ? null : values.get(0);
        if (name == null) { return null; }
        boolean exists = fields != null && fields.stream().anyMatch(f -> name.equals(f.getFieldName()));
        if (!exists) { return null; }
        com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum op = cond.getOperator();
        if (op == null) {
            if (val == null) { return null; }
            return (val instanceof String) ? new QueryColumn(name).like(val) : new QueryColumn(name).eq(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.EQ) {
            return new QueryColumn(name).eq(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.NE) {
            return new QueryColumn(name).ne(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.GT) {
            return new QueryColumn(name).gt(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.GE) {
            return new QueryColumn(name).ge(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.LT) {
            return new QueryColumn(name).lt(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.LE) {
            return new QueryColumn(name).le(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.LIKE) {
            return new QueryColumn(name).like(val);
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.IN) {
            if (values != null && !values.isEmpty()) { return new QueryColumn(name).in(values); }
            return null;
        } else if (op == com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum.NIN) {
            if (values != null && !values.isEmpty()) { return new QueryColumn(name).notIn(values); }
            return null;
        } else {
            if (val == null) { return null; }
            return (val instanceof String) ? new QueryColumn(name).like(val) : new QueryColumn(name).eq(val);
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
