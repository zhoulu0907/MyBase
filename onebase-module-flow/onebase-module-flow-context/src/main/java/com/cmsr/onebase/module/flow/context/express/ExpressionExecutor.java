package com.cmsr.onebase.module.flow.context.express;

import com.cmsr.onebase.module.flow.context.enums.OperatorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 表达式助手类
 * 整合了表达式引擎和规则评估器的功能
 * 负责评估条件和规则的逻辑关系
 *
 * @Author：huangjie
 * @Date：2025/9/16 21:11
 */
@Slf4j
public class ExpressionExecutor implements Serializable {

    private JexlEngine jexlEngine;

    public ExpressionExecutor() {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        this.jexlEngine = new JexlBuilder().permissions(permissions).arithmetic(new ExtJexlArithmetic(false)).silent(false).create();
    }

    public boolean evaluateInput(OrExpression orExpression, Map<String, Object> vars) {
        return evaluate(orExpression, vars);
    }

    public boolean evaluateContext(OrExpression orExpression, Map<String, Object> vars) {
        formatFieldKeyForContext(orExpression);
        return evaluate(orExpression, vars);
    }

    private void formatFieldKeyForContext(OrExpression orExpression) {
        for (AndExpression andExpression : orExpression.getAndExpressions()) {
            for (ExpressionItem expressionItem : andExpression.getExpressionItems()) {
                expressionItem.setFieldKey(formatItemKey(expressionItem.getFieldKey()));
            }
        }
    }

    /**
     * 评估条件
     * 优化版本：将整个条件结构转换成一个大表达式一次性执行
     * 根据Condition类的注释：条件项之间是OR关系
     * 根据ConditionItem类的注释：规则项之间是AND关系
     */
    private boolean evaluate(OrExpression orExpression, Map<String, Object> vars) {
        String fullExpression = null;
        try {
            fullExpression = buildConditionExpression(orExpression);
            JexlExpression expression = jexlEngine.createExpression(fullExpression);
            MapContext jc = new MapContext(vars);
            Boolean result = (Boolean) expression.evaluate(jc);
            if (result == null) {
                log.warn("表达式执行结果为空, 执行表达式: {}, 输入参数: {}", fullExpression, vars);
                return false;
            } else {
                return result;
            }
        } catch (Exception e) {
            String msg = "表达式执行异常, 执行表达式: " + fullExpression + ", 输入参数:" + vars;
            throw new RuntimeException(msg, e);
        }
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
        if (expressionItem == null || expressionItem.getOp() == null) {
            return "true";
        }
        String expression = buildExpression(expressionItem);
        return expression;
    }

    private String formatItemKey(String key) {
        if (key.contains(".")) {
            String key1 = StringUtils.substringBefore(key, ".");
            String key2 = StringUtils.substringAfter(key, ".");
            if (!key2.startsWith("[") || !key2.endsWith("]")) {
                return String.format("%s['%s']", key1, key2);
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
        expressionItem.setFieldKey(expressionItem.getFieldKey());
        switch (expressionItem.getOp()) {
            case EQUALS:
                return String.format("%s == %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case NOT_EQUALS:
                return String.format("%s != %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case CONTAINS:
                return String.format("%s.contains(%s)", expressionItem.getFieldKey(), formatValue(expressionItem));

            case NOT_CONTAINS:
                return String.format("!(%s.contains(%s))", expressionItem.getFieldKey(), formatValue(expressionItem));

            case EXISTS_IN:
                return String.format("%s.contains(%s)", formatValue(expressionItem), expressionItem.getFieldKey());

            case NOT_EXISTS_IN:
                return String.format("!(%s.contains(%s))", formatValue(expressionItem), expressionItem.getFieldKey());

            case GREATER_THAN:
                return String.format("%s > %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case GREATER_EQUALS:
                return String.format("%s >= %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case LESS_THAN:
                return String.format("%s < %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case LESS_EQUALS:
                return String.format("%s <= %s", expressionItem.getFieldKey(), formatValue(expressionItem));

            case LATER_THAN:
                return String.format("%s > %s", expressionItem.getFieldKey(), formatDateValue(expressionItem));

            case EARLIER_THAN:
                return String.format("%s < %s", expressionItem.getFieldKey(), formatDateValue(expressionItem));

            case RANGE:
                if (expressionItem.getFieldValue() instanceof List list) {
                    return String.format("(%s >= %s && %s <= %s)",
                            expressionItem.getFieldKey(), formatValue(list.get(0), expressionItem),
                            expressionItem.getFieldKey(), formatValue(list.get(1), expressionItem));
                }
                throw new IllegalArgumentException("RANGE操作需要两个值");

            case IS_EMPTY:
                return String.format("(%s == null || %s.isEmpty())", expressionItem.getFieldKey(), expressionItem.getFieldKey());

            case IS_NOT_EMPTY:
                return String.format("(%s != null && !%s.isEmpty())", expressionItem.getFieldKey(), expressionItem.getFieldKey());

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
        List listValue = (List) expressionItem.getFieldValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" && ");
            }
            sb.append(String.format("%s contains %s", expressionItem.getFieldKey(), formatValue(listValue.get(i), expressionItem)));
        }
        return sb.toString();
    }

    /**
     * 构建包含任一的表达式
     */
    private String buildContainsAnyExpression(ExpressionItem expressionItem) {
        List listValue = (List) expressionItem.getFieldValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append(String.format("%s contains %s", expressionItem.getFieldKey(), formatValue(listValue.get(i), expressionItem)));
        }
        return sb.toString();
    }

    private String formatValue(Object value, ExpressionItem expressionItem) {
        ExpressionItem newExpressionItem = new ExpressionItem();
        newExpressionItem.setFieldKey(expressionItem.getFieldKey());
        newExpressionItem.setOp(expressionItem.getOp());
        newExpressionItem.setFieldValue(value);
        newExpressionItem.setOperatorType(expressionItem.getOperatorType());
        newExpressionItem.setFieldTypeEnum(expressionItem.getFieldTypeEnum());
        return formatValue(newExpressionItem);

    }

    /**
     * 格式化值为表达式字符串
     */
    private String formatValue(ExpressionItem expressionItem) {
        if (expressionItem.getFieldValue() == null) {
            return "null";
        }
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            return formatItemKey(expressionItem.getFieldValue().toString());
        }
        if (expressionItem.getFieldValue() instanceof Collection) {
            return formatCollectionValue((Collection<?>) expressionItem.getFieldValue(), expressionItem);
        }
        if (expressionItem.getFieldTypeEnum().getRawJavaType() == String.class) {
            return "'" + expressionItem.getFieldValue().toString().replace("'", "\\'") + "'";
        }
        if (expressionItem.getFieldTypeEnum().getRawJavaType() == BigDecimal.class
                || expressionItem.getFieldTypeEnum().getRawJavaType() == Long.class) {
            return expressionItem.getFieldValue().toString();
        }
        if (expressionItem.getFieldTypeEnum().getRawJavaType() == LocalDate.class
                || expressionItem.getFieldTypeEnum().getRawJavaType() == LocalDateTime.class) {
            return formatDateValue(expressionItem);
        }
        return "'" + expressionItem.getFieldValue() + "'";
    }

    /**
     * 格式化日期值
     */
    private String formatDateValue(ExpressionItem expressionItem) {
        if (expressionItem.getFieldValue() instanceof LocalDate) {
            LocalDate date = (LocalDate) expressionItem.getFieldValue();
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (expressionItem.getFieldValue() instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) expressionItem.getFieldValue();
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (expressionItem.getFieldValue() instanceof LocalTime) {
            LocalTime time = (LocalTime) expressionItem.getFieldValue();
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return "'" + expressionItem.getFieldValue().toString() + "'";
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
