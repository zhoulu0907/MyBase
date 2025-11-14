package com.cmsr.onebase.module.flow.context.express;

import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 表达式助手类
 * 整合了表达式引擎和规则评估器的功能
 * 负责评估条件和规则的逻辑关系
 *
 * @Author：huangjie
 * @Date：2025/9/16 21:11
 */
public class ExpressionExecutor implements Serializable {

    public static final String VAR_PREFIX = "var_";

    private JexlEngine jexlEngine;

    public ExpressionExecutor() {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        this.jexlEngine = new JexlBuilder().permissions(permissions).arithmetic(new ExtJexlArithmetic(true)).silent(false).create();
    }

    /**
     * 评估条件
     * 优化版本：将整个条件结构转换成一个大表达式一次性执行
     * 根据Condition类的注释：条件项之间是OR关系
     * 根据ConditionItem类的注释：规则项之间是AND关系
     */
    public boolean evaluate(OrExpression orExpression, Map<String, Object> vars) {
        try {
            String fullExpression = buildConditionExpression(orExpression);
            JexlExpression expression = jexlEngine.createExpression(fullExpression);
            Map<String, Object> expressionContext = formatMapContextKey(vars);
            MapContext jc = new MapContext(expressionContext);
            Object result = expression.evaluate(jc);
            return result instanceof Boolean ? (Boolean) result : Boolean.FALSE;
        } catch (Exception e) {
            String msg = "表达式执行异常, 执行表达式:" + orExpression + ", 输入条件:" + vars + "";
            throw new RuntimeException(msg, e);
        }
    }

    private Map<String, Object> formatMapContextKey(Map<String, Object> context) {
        Map<String, Object> newContext = new HashMap<>();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isNumeric(key)) {
                newContext.put(VAR_PREFIX + key, value);
            } else {
                newContext.put(key, value);
            }
        }
        return newContext;
    }

    /**
     * 构建完整的条件表达式
     * 将整个OrExpresses转换成一个表达式字符串
     *
     * @param orExpression OR表达式集合
     * @return 表达式字符串
     */
    private String buildConditionExpression(OrExpression orExpression) {
        if (orExpression == null || CollectionUtils.isEmpty(orExpression.getAndExpressions())) {
            return "true";
        }
        List<String> conditionExpressions = new ArrayList<>();
        // 遍历所有AND表达式组
        for (AndExpression andExpresses : orExpression.getAndExpressions()) {
            String andExpression = buildAndExpression(andExpresses);
            if (andExpression != null && !andExpression.trim().isEmpty()) {
                conditionExpressions.add("(" + andExpression + ")");
            }
        }
        if (conditionExpressions.isEmpty()) {
            return "true";
        }
        // AND表达式组之间用OR连接
        return String.join(" || ", conditionExpressions);
    }

    /**
     * 构建AND表达式
     *
     * @param andExpression AND表达式对象
     * @return 表达式字符串
     */
    private String buildAndExpression(AndExpression andExpression) {
        if (andExpression == null || CollectionUtils.isEmpty(andExpression.getExpressionItems())) {
            return "true";
        }
        List<String> expressionItems = new ArrayList<>();
        // 遍历所有表达式项
        for (ExpressionItem expressionItem : andExpression.getExpressionItems()) {
            String itemExpression = buildExpressItemExpression(expressionItem);
            if (itemExpression != null && !itemExpression.trim().isEmpty()) {
                expressionItems.add(itemExpression);
            }
        }
        if (expressionItems.isEmpty()) {
            return "true";
        }
        // 表达式项之间用AND连接
        return String.join(" && ", expressionItems);
    }

    /**
     * 构建单个表达式项的表达式
     *
     * @param expressionItem 表达式项
     * @return 表达式字符串
     */
    private String buildExpressItemExpression(ExpressionItem expressionItem) {
        if (expressionItem == null || expressionItem.getKey() == null || expressionItem.getOp() == null) {
            return "true";
        }
        String expression = buildExpression(expressionItem);
        return expression;
    }

    private String formatItemKey(String key) {
        if (StringUtils.isNumeric(key)) {
            return VAR_PREFIX + key;
        }
        if (key.contains(".")) {
            String[] ss = StringUtils.split(key, ".");
            if (!ss[1].startsWith("'") || !ss[1].endsWith("'")) {
                return String.format("%s.'%s'", ss[0], ss[1]);
            }
        }
        return key;
    }


    /**
     * 根据操作符和参数构建表达式
     *
     * @return 表达式字符串
     */
    public String buildExpression(ExpressionItem expressionItem) {
        expressionItem = ExpressionItem.copy(expressionItem);
        expressionItem.setKey(formatItemKey(expressionItem.getKey()));
        switch (expressionItem.getOp()) {
            case EQUALS:
                return String.format("%s == %s", expressionItem.getKey(), formatValue(expressionItem));

            case NOT_EQUALS:
                return String.format("%s != %s", expressionItem.getKey(), formatValue(expressionItem));

            case CONTAINS:
                return String.format("%s.contains(%s)", expressionItem.getKey(), formatValue(expressionItem));

            case NOT_CONTAINS:
                return String.format("!(%s.contains(%s))", expressionItem.getKey(), formatValue(expressionItem));

            case EXISTS_IN:
                return String.format("%s.contains(%s)", formatValue(expressionItem), expressionItem.getKey());

            case NOT_EXISTS_IN:
                return String.format("!(%s.contains(%s))", formatValue(expressionItem), expressionItem.getKey());

            case GREATER_THAN:
                return String.format("%s > %s", expressionItem.getKey(), formatValue(expressionItem));

            case GREATER_EQUALS:
                return String.format("%s >= %s", expressionItem.getKey(), formatValue(expressionItem));

            case LESS_THAN:
                return String.format("%s < %s", expressionItem.getKey(), formatValue(expressionItem));

            case LESS_EQUALS:
                return String.format("%s <= %s", expressionItem.getKey(), formatValue(expressionItem));

            case LATER_THAN:
                return String.format("%s > %s", expressionItem.getKey(), formatDateValue(expressionItem));

            case EARLIER_THAN:
                return String.format("%s < %s", expressionItem.getKey(), formatDateValue(expressionItem));

            case RANGE:
                if (expressionItem.getValue() instanceof List list) {
                    return String.format("(%s >= %s && %s <= %s)",
                            expressionItem.getKey(), formatValue(list.get(0), expressionItem),
                            expressionItem.getKey(), formatValue(list.get(1), expressionItem));
                }
                throw new IllegalArgumentException("RANGE操作需要两个值");

            case IS_EMPTY:
                return String.format("(%s == null || %s.isEmpty())", expressionItem.getKey(), expressionItem.getKey());

            case IS_NOT_EMPTY:
                return String.format("(%s != null && !%s.isEmpty())", expressionItem.getKey(), expressionItem.getKey());

            case CONTAINS_ALL:
                return buildContainsAllExpression(expressionItem);

            case NOT_CONTAINS_ALL:
                return String.format("!(%s)", buildContainsAllExpression(expressionItem));

            case CONTAINS_ANY:
                return buildContainsAnyExpression(expressionItem);

            case NOT_CONTAINS_ANY:
                return String.format("!(%s)", buildContainsAnyExpression(expressionItem));

            default:
                throw new UnsupportedOperationException("不支持的操作符: " + expressionItem.getOp());
        }
    }


    /**
     * 构建包含全部的表达式
     */
    private String buildContainsAllExpression(ExpressionItem expressionItem) {
        List listValue = (List) expressionItem.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" && ");
            }
            sb.append(String.format("%s contains %s", expressionItem.getKey(), formatValue(listValue.get(i), expressionItem)));
        }
        return sb.toString();
    }

    /**
     * 构建包含任一的表达式
     */
    private String buildContainsAnyExpression(ExpressionItem expressionItem) {
        List listValue = (List) expressionItem.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append(String.format("%s contains %s", expressionItem.getKey(), formatValue(listValue.get(i), expressionItem)));
        }
        return sb.toString();
    }

    private String formatValue(Object value, ExpressionItem expressionItem) {
        ExpressionItem newExpressionItem = new ExpressionItem();
        newExpressionItem.setKey(expressionItem.getKey());
        newExpressionItem.setOp(expressionItem.getOp());
        newExpressionItem.setValue(value);
        newExpressionItem.setJdbcType(expressionItem.getJdbcType());
        newExpressionItem.setOperatorType(expressionItem.getOperatorType());
        newExpressionItem.setFieldType(expressionItem.getFieldType());
        return formatValue(newExpressionItem);

    }

    /**
     * 格式化值为表达式字符串
     */
    private String formatValue(ExpressionItem expressionItem) {
        if (expressionItem.getValue() == null) {
            return "null";
        }
        if (expressionItem.getValue() instanceof Collection) {
            return formatCollectionValue((Collection<?>) expressionItem.getValue(), expressionItem);
        }
        if (expressionItem.getJdbcType() == JdbcTypeEnum.VARCHAR
                || expressionItem.getJdbcType() == JdbcTypeEnum.LONGVARCHAR) {
            return "'" + expressionItem.getValue().toString().replace("'", "\\'") + "'";
        }
        if (expressionItem.getJdbcType() == JdbcTypeEnum.BOOLEAN
                || expressionItem.getJdbcType() == JdbcTypeEnum.BIGINT
                || expressionItem.getJdbcType() == JdbcTypeEnum.NUMERIC
                || expressionItem.getJdbcType() == JdbcTypeEnum.DECIMAL) {
            return expressionItem.getValue().toString();
        }
        if (expressionItem.getJdbcType() == JdbcTypeEnum.TIMESTAMP
                || expressionItem.getJdbcType() == JdbcTypeEnum.DATE) {
            return formatDateValue(expressionItem);
        }
        return "'" + expressionItem.getValue() + "'";
    }

    /**
     * 格式化日期值
     */
    private String formatDateValue(ExpressionItem expressionItem) {
        if (expressionItem.getValue() instanceof LocalDate) {
            LocalDate date = (LocalDate) expressionItem.getValue();
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (expressionItem.getValue() instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) expressionItem.getValue();
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (expressionItem.getValue() instanceof LocalTime) {
            LocalTime time = (LocalTime) expressionItem.getValue();
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return "'" + expressionItem.getValue().toString() + "'";
    }

    /**
     * 格式化集合值
     */
    private String formatCollectionValue(Collection<?> collection, ExpressionItem expressionItem) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(formatValue(item, expressionItem));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

}
