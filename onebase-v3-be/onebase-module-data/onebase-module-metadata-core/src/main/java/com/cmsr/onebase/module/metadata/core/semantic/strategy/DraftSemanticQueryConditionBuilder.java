package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.*;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class DraftSemanticQueryConditionBuilder {

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
            QueryCondition strCond = new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0);
            qw.where(strCond);
        }

/*        if (hasDefaultStatusField(fields)) {
            QueryCondition strCond = new QueryColumn(SystemFieldConstants.OPTIONAL.DRAFT_STATUS).eq(1);
            qw.where(strCond);
        }*/
    }

    private QueryCondition buildQueryCondition(SemanticConditionDTO cond, List<SemanticFieldSchemaDTO> fields) {
        if (cond == null) { return null; }
        List<SemanticConditionDTO> children = cond.getChildren();
        boolean markedGroup = cond.getNodeType() == SemanticConditionNodeTypeEnum.GROUP;
        if (markedGroup || (children != null && !children.isEmpty())) {
            QueryCondition group = null;
            boolean and = cond.getCombinator() == null || cond.getCombinator() == SemanticCombinatorEnum.AND;
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
        SemanticOperatorEnum op = cond.getOperator();
        var type = resolveFieldType(name, fields);
        if (op == null) {
            if (val == null) { return null; }
            Object casted = castValue(val, type);
            if (casted instanceof String) { return new QueryColumn(name).like(casted); }
            return new QueryColumn(name).eq(casted);
        } else if (op == SemanticOperatorEnum.EQUALS) {
            return new QueryColumn(name).eq(castValue(val, type));
        } else if (op == SemanticOperatorEnum.NOT_EQUALS) {
            return new QueryColumn(name).ne(castValue(val, type));
        } else if (op == SemanticOperatorEnum.GREATER_THAN) {
            return new QueryColumn(name).gt(castValue(val, type));
        } else if (op == SemanticOperatorEnum.GREATER_EQUALS) {
            return new QueryColumn(name).ge(castValue(val, type));
        } else if (op == SemanticOperatorEnum.LESS_THAN) {
            return new QueryColumn(name).lt(castValue(val, type));
        } else if (op == SemanticOperatorEnum.LESS_EQUALS) {
            return new QueryColumn(name).le(castValue(val, type));
        } else if (op == SemanticOperatorEnum.CONTAINS) {
            Object v = val == null ? null : String.valueOf(val);
            return new QueryColumn(name).like(v);
        } else if (op == SemanticOperatorEnum.EXISTS_IN) {
            if (values != null && !values.isEmpty()) { return new QueryColumn(name).in(castValues(values, type)); }
            return null;
        } else if (op == SemanticOperatorEnum.NOT_EXISTS_IN) {
            if (values != null && !values.isEmpty()) { return new QueryColumn(name).notIn(castValues(values, type)); }
            return null;
        } else if (op == SemanticOperatorEnum.NOT_CONTAINS) {
            if (val == null) { return null; }
            Object v = String.valueOf(val);
            return new QueryColumn(name).notLike(v);
        } else if (op == SemanticOperatorEnum.LATER_THAN) {
            return new QueryColumn(name).gt(castValue(val, type));
        } else if (op == SemanticOperatorEnum.EARLIER_THAN) {
            return new QueryColumn(name).lt(castValue(val, type));
        } else if (op == SemanticOperatorEnum.RANGE) {
            if (values == null || values.isEmpty()) { return null; }
            Object start = values.size() > 0 ? values.get(0) : null;
            Object end = values.size() > 1 ? values.get(1) : null;
            QueryCondition c = null;
            Object sv = castValue(start, type);
            Object ev = castValue(end, type);
            if (sv != null) { c = new QueryColumn(name).ge(sv); }
            if (ev != null) { c = (c == null) ? new QueryColumn(name).le(ev) : c.and(new QueryColumn(name).le(ev)); }
            return c;
        } else if (op == SemanticOperatorEnum.CONTAINS_ALL) {
            if (values == null || values.isEmpty()) { return null; }
            QueryCondition c = null;
            for (Object v : values) {
                if (v == null) { continue; }
                QueryCondition likeCond = new QueryColumn(name).like(String.valueOf(v));
                c = (c == null) ? likeCond : c.and(likeCond);
            }
            return c;
        } else if (op == SemanticOperatorEnum.CONTAINS_ANY) {
            if (values == null || values.isEmpty()) { return null; }
            QueryCondition c = null;
            for (Object v : values) {
                if (v == null) { continue; }
                QueryCondition likeCond = new QueryColumn(name).like(String.valueOf(v));
                c = (c == null) ? likeCond : c.or(likeCond);
            }
            return c;
        } else if (op == SemanticOperatorEnum.NOT_CONTAINS_ALL) {
            if (values == null || values.isEmpty()) { return null; }
            QueryCondition c = null;
            for (Object v : values) {
                if (v == null) { continue; }
                QueryCondition notLikeCond = new QueryColumn(name).notLike(String.valueOf(v));
                c = (c == null) ? notLikeCond : c.or(notLikeCond);
            }
            return c;
        } else if (op == SemanticOperatorEnum.NOT_CONTAINS_ANY) {
            if (values == null || values.isEmpty()) { return null; }
            QueryCondition c = null;
            for (Object v : values) {
                if (v == null) { continue; }
                QueryCondition notLikeCond = new QueryColumn(name).notLike(String.valueOf(v));
                c = (c == null) ? notLikeCond : c.and(notLikeCond);
            }
            return c;
        } else if (op == SemanticOperatorEnum.IS_EMPTY) {
            QueryCondition c1 = new QueryColumn(name).isNull();
            QueryCondition c2 = new QueryColumn(name).eq("");
            return c1.or(c2);
        } else if (op == SemanticOperatorEnum.IS_NOT_EMPTY) {
            QueryCondition c1 = new QueryColumn(name).isNotNull();
            QueryCondition c2 = new QueryColumn(name).ne("");
            return c1.and(c2);
        } else {
            if (val == null) { return null; }
            Object casted = castValue(val, type);
            if (casted instanceof String) { return new QueryColumn(name).like(casted); }
            return new QueryColumn(name).eq(casted);
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
        return fields.stream().anyMatch(f -> SystemFieldConstants.OPTIONAL.DELETED.equalsIgnoreCase(f.getFieldName()));
    }

/*    private boolean hasDefaultStatusField(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) { return false; }
        return fields.stream().anyMatch(f -> SystemFieldConstants.OPTIONAL.DRAFT_STATUS.equalsIgnoreCase(f.getFieldName()));
    }*/

    private SemanticFieldTypeEnum resolveFieldType(String fieldName, List<SemanticFieldSchemaDTO> fields) {
        if (fieldName == null || fields == null) { return null; }
        Optional<SemanticFieldTypeEnum> t = fields.stream()
                .filter(f -> f != null && fieldName.equals(f.getFieldName()))
                .map(SemanticFieldSchemaDTO::getFieldTypeEnum)
                .filter(Objects::nonNull)
                .findFirst();
        return t.orElse(null);
    }

    private Object castValue(Object v, SemanticFieldTypeEnum type) {
        if (v == null || type == null) { return v; }
        switch (type) {
            case ID:
                    return Long.valueOf(String.valueOf(v).trim());
            case USER:
            case DEPARTMENT:
            case DATA_SELECTION:
                // 使用 String 类型以兼容数据库中 text 类型的引用字段
                return String.valueOf(v).trim();
            case NUMBER:
            case AGGREGATE:
                if (v instanceof BigDecimal) { return v; }
                if (v instanceof Number) { return new BigDecimal(String.valueOf(((Number) v))); }
                try { return new BigDecimal(String.valueOf(v).trim()); } catch (Exception e) { return null; }
            case DATE:
                if (v instanceof LocalDate) { return v; }
                try { return LocalDate.parse(String.valueOf(v).trim()); } catch (Exception e) { return null; }
            case DATETIME:
                if (v instanceof LocalDateTime) { return v; }
                try {
                    String s = String.valueOf(v).trim();
                    if (s.contains(" ")) { s = s.replace(" ", "T"); }
                    return LocalDateTime.parse(s);
                } catch (Exception e) { return null; }
            case BOOLEAN:
                if (v instanceof Boolean) { return v; }
                String s = String.valueOf(v).trim().toLowerCase();
                if ("true".equals(s) || "1".equals(s)) { return true; }
                if ("false".equals(s) || "0".equals(s)) { return false; }
                return null;
            default:
                return String.valueOf(v);
        }
    }

    private List<Object> castValues(List<Object> values, SemanticFieldTypeEnum type) {
        if (values == null || values.isEmpty()) { return values; }
        java.util.ArrayList<Object> list = new java.util.ArrayList<>(values.size());
        for (Object v : values) { list.add(castValue(v, type)); }
        return list;
    }
}
