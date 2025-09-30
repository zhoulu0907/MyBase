package com.cmsr.onebase.module.flow.context.express;

import com.cmsr.onebase.framework.common.express.JdbcTypeEnum;
import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 表达式助手类
 * 整合了表达式引擎和规则评估器的功能
 * 负责评估条件和规则的逻辑关系
 *
 * @Author：huangjie
 * @Date：2025/9/16 21:11
 */
@Slf4j
@Component
public class ExpressionExecutor {

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
    public boolean evaluate(JexlExpression expression, Map<String, Object> context) {
        try {
            MapContext jc = new MapContext(formatMapContextKey(context));
            Object result = expression.evaluate(jc);
            return result instanceof Boolean ? (Boolean) result : Boolean.FALSE;
        } catch (Exception e) {
            log.error("条件评估失败: {}", expression, e);
            return false;
        }
    }

    private Map<String, Object> formatMapContextKey(Map<String, Object> context) {
        Map<String, Object> newContext = new HashMap<>();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isNumeric(key)) {
                newContext.put("var_" + key, value);
            } else {
                newContext.put(key, value);
            }
        }
        return newContext;
    }

    public JexlExpression compileExpression(OrExpresses orExpresses) {
        String fullExpression = buildConditionExpression(orExpresses);
        return jexlEngine.createExpression(fullExpression);
    }

    /**
     * 构建完整的条件表达式
     * 将整个OrExpresses转换成一个表达式字符串
     *
     * @param orExpresses OR表达式集合
     * @return 表达式字符串
     */
    private String buildConditionExpression(OrExpresses orExpresses) {
        if (orExpresses == null || CollectionUtils.isEmpty(orExpresses.getExpressesList())) {
            return "true";
        }
        List<String> conditionExpressions = new ArrayList<>();
        // 遍历所有AND表达式组
        for (AndExpresses andExpresses : orExpresses.getExpressesList()) {
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
     * @param andExpresses AND表达式对象
     * @return 表达式字符串
     */
    private String buildAndExpression(AndExpresses andExpresses) {
        if (andExpresses == null || CollectionUtils.isEmpty(andExpresses.getExpressItems())) {
            return "true";
        }
        List<String> expressionItems = new ArrayList<>();
        // 遍历所有表达式项
        for (ExpressItem expressItem : andExpresses.getExpressItems()) {
            String itemExpression = buildExpressItemExpression(expressItem);
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
     * @param expressItem 表达式项
     * @return 表达式字符串
     */
    private String buildExpressItemExpression(ExpressItem expressItem) {
        try {
            if (expressItem == null || expressItem.getKey() == null || expressItem.getOp() == null) {
                return "true";
            }

            String expression = buildExpression(expressItem);

            log.debug("构建表达式项: key={}, operator={} -> {}", expressItem.getKey(), expressItem.getOp(), expression);

            return expression;

        } catch (Exception e) {
            log.error("表达式项构建失败: {}", expressItem, e);
            // 返回false表达式，确保整个条件不会因为单个表达式失败而崩溃
            return "false";
        }
    }


    /**
     * 根据操作符和参数构建表达式
     *
     * @return 表达式字符串
     */
    public String buildExpression(ExpressItem expressItem) {
        expressItem = ExpressItem.copy(expressItem);
        if (StringUtils.isNumeric(expressItem.getKey().toString())) {
            expressItem.setKey("var_" + expressItem.getKey());
        }
        switch (expressItem.getOp()) {
            case EQUALS:
                return String.format("%s == %s", expressItem.getKey(), formatValue(expressItem));

            case NOT_EQUALS:
                return String.format("%s != %s", expressItem.getKey(), formatValue(expressItem));

            case CONTAINS:
                return String.format("%s.contains(%s)", expressItem.getKey(), formatValue(expressItem));

            case NOT_CONTAINS:
                return String.format("!(%s.contains(%s))", expressItem.getKey(), formatValue(expressItem));

            case EXISTS_IN:
                return String.format("%s.contains(%s)", formatValue(expressItem), expressItem.getKey());

            case NOT_EXISTS_IN:
                return String.format("!(%s.contains(%s))", formatValue(expressItem), expressItem.getKey());

            case GREATER_THAN:
                return String.format("%s > %s", expressItem.getKey(), formatValue(expressItem));

            case GREATER_EQUALS:
                return String.format("%s >= %s", expressItem.getKey(), formatValue(expressItem));

            case LESS_THAN:
                return String.format("%s < %s", expressItem.getKey(), formatValue(expressItem));

            case LESS_EQUALS:
                return String.format("%s <= %s", expressItem.getKey(), formatValue(expressItem));

            case LATER_THAN:
                return String.format("%s > %s", expressItem.getKey(), formatDateValue(expressItem));

            case EARLIER_THAN:
                return String.format("%s < %s", expressItem.getKey(), formatDateValue(expressItem));

            case RANGE:
                if (expressItem.getValue() instanceof List list) {
                    return String.format("%s >= %s && %s <= %s",
                            expressItem.getKey(), formatValue(list.get(0), expressItem),
                            expressItem.getKey(), formatValue(list.get(1), expressItem));
                }
                throw new IllegalArgumentException("RANGE操作需要两个值");

            case IS_EMPTY:
                return String.format("(%s == null || %s.isEmpty())", expressItem.getKey(), expressItem.getKey());

            case IS_NOT_EMPTY:
                return String.format("(%s != null && !%s.isEmpty())", expressItem.getKey(), expressItem.getKey());

            case CONTAINS_ALL:
                return buildContainsAllExpression(expressItem);

            case NOT_CONTAINS_ALL:
                return String.format("!(%s)", buildContainsAllExpression(expressItem));

            case CONTAINS_ANY:
                return buildContainsAnyExpression(expressItem);

            case NOT_CONTAINS_ANY:
                return String.format("!(%s)", buildContainsAnyExpression(expressItem));

            default:
                throw new UnsupportedOperationException("不支持的操作符: " + expressItem.getOp());
        }
    }


    /**
     * 构建包含全部的表达式
     */
    private String buildContainsAllExpression(ExpressItem expressItem) {
        List listValue = (List) expressItem.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" && ");
            }
            sb.append(String.format("%s contains %s", expressItem.getKey(), formatValue(listValue.get(i), expressItem)));
        }
        return sb.toString();
    }

    /**
     * 构建包含任一的表达式
     */
    private String buildContainsAnyExpression(ExpressItem expressItem) {
        List listValue = (List) expressItem.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listValue.size(); i++) {
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append(String.format("%s contains %s", expressItem.getKey(), formatValue(listValue.get(i), expressItem)));
        }
        return sb.toString();
    }

    private String formatValue(Object value, ExpressItem expressItem) {
        ExpressItem newExpressItem = new ExpressItem();
        newExpressItem.setKey(expressItem.getKey());
        newExpressItem.setOp(expressItem.getOp());
        newExpressItem.setValue(value);
        //
        newExpressItem.setJdbcType(expressItem.getJdbcType());
        newExpressItem.setOperatorType(expressItem.getOperatorType());
        newExpressItem.setFieldType(expressItem.getFieldType());
        return buildExpression(newExpressItem);

    }

    /**
     * 格式化值为表达式字符串
     */
    private String formatValue(ExpressItem expressItem) {
        if (expressItem.getValue() == null) {
            return "null";
        }
        if (expressItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            return expressItem.getValue().toString();
        }
        if (expressItem.getJdbcType() == JdbcTypeEnum.VARCHAR) {
            return "'" + expressItem.getValue().toString().replace("'", "\\'") + "'";
        }
        if (expressItem.getValue() instanceof Collection) {
            return formatCollectionValue((Collection<?>) expressItem.getValue(), expressItem);
        }
        if (expressItem.getJdbcType() == JdbcTypeEnum.BOOLEAN
                || expressItem.getJdbcType() == JdbcTypeEnum.BIGINT
                || expressItem.getJdbcType() == JdbcTypeEnum.NUMERIC
                || expressItem.getJdbcType() == JdbcTypeEnum.DECIMAL) {
            return expressItem.getValue().toString();
        }

        return "'" + expressItem.getValue() + "'";
    }

    /**
     * 格式化日期值
     */
    private String formatDateValue(ExpressItem expressItem) {
        if (expressItem.getValue() instanceof LocalDate) {
            LocalDate date = (LocalDate) expressItem.getValue();
            return String.format("LocalDate.of(%d, %d, %d)",
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (expressItem.getValue() instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) expressItem.getValue();
            return String.format("LocalDateTime.of(%d, %d, %d, %d, %d, %d)",
                    dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
                    dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        }
        if (expressItem.getValue() instanceof LocalTime) {
            LocalTime time = (LocalTime) expressItem.getValue();
            return String.format("LocalTime.of(%d, %d, %d)",
                    time.getHour(), time.getMinute(), time.getSecond());
        }
        return formatValue(expressItem.getValue(), expressItem);
    }

    /**
     * 格式化集合值
     */
    private String formatCollectionValue(Collection<?> collection, ExpressItem expressItem) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(formatValue(item, expressItem));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

}
