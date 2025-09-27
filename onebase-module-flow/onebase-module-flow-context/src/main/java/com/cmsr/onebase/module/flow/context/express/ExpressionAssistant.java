package com.cmsr.onebase.module.flow.context.express;

import com.cmsr.onebase.framework.common.express.OpEnum;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Array;
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
public class ExpressionAssistant {

    /**
     * MVEL解析器上下文
     */
    private final ParserContext parserContext;

    public ExpressionAssistant() {
        this.parserContext = new ParserContext();
        // 导入常用的Java类
        parserContext.addImport("LocalDate", LocalDate.class);
        parserContext.addImport("LocalDateTime", LocalDateTime.class);
        parserContext.addImport("LocalTime", LocalTime.class);
        parserContext.addImport("Arrays", Arrays.class);
        parserContext.addImport("Collections", Collections.class);
    }

    /**
     * 评估条件
     * 优化版本：将整个条件结构转换成一个大表达式一次性执行
     * 根据Condition类的注释：条件项之间是OR关系
     * 根据ConditionItem类的注释：规则项之间是AND关系
     *
     * @param compiled 条件对象
     * @param context  上下文数据
     * @return 评估结果
     */
    public boolean evaluate(Serializable compiled, Map<String, Object> context) {
        try {
            // 一次性执行整个表达式
            Object result = MVEL.executeExpression(compiled, context);
            return result instanceof Boolean ? (Boolean) result : Boolean.FALSE;
        } catch (Exception e) {
            log.error("条件评估失败: {}", compiled, e);
            return false;
        }
    }

    public Serializable compileExpression(OrExpresses orExpresses) {
        String fullExpression = buildConditionExpression(orExpresses);
        return MVEL.compileExpression(fullExpression, parserContext);
    }

    /**
     * 构建完整的条件表达式
     * 将整个OrExpresses转换成一个表达式字符串
     *
     * @param orExpresses OR表达式集合
     * @return 表达式字符串
     */
    private String buildConditionExpression(OrExpresses orExpresses) {
        if (CollectionUtils.isEmpty(orExpresses.getExpressesList())) {
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
            if (expressItem == null || expressItem.getKey() == null || expressItem.getOperatorType() == null) {
                return "true";
            }

            // 获取字段名
            String fieldName = expressItem.getKey().toString();

            // 获取操作符
            OpEnum operator = OpEnum.valueOf(expressItem.getOperatorType());

            // 构建表达式
            String expression = buildExpression(fieldName, operator, expressItem.getValue());

            log.debug("构建表达式项: key={}, operator={} -> {}", expressItem.getKey(), expressItem.getOperatorType(), expression);

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
     * @param fieldName 字段名
     * @param operator  操作符
     * @param value     值列表
     * @return 表达式字符串
     */
    public String buildExpression(String fieldName, OpEnum operator, Object value) {
        switch (operator) {
            case EQUALS:
                return String.format("%s == %s", fieldName, formatValue(value));

            case NOT_EQUALS:
                return String.format("%s != %s", fieldName, formatValue(value));

            case CONTAINS:
                return String.format("%s contains %s", fieldName, formatValue(value));

            case NOT_CONTAINS:
                return String.format("!(%s contains %s)", fieldName, formatValue(value));

            case EXISTS_IN:
                return String.format("%s contains %s", formatValue(value), fieldName);

            case NOT_EXISTS_IN:
                return String.format("!(%s contains %s)", formatValue(value), fieldName);

            case GREATER_THAN:
                return String.format("%s > %s", fieldName, formatValue(value));

            case GREATER_EQUALS:
                return String.format("%s >= %s", fieldName, formatValue(value));

            case LESS_THAN:
                return String.format("%s < %s", fieldName, formatValue(value));

            case LESS_EQUALS:
                return String.format("%s <= %s", fieldName, formatValue(value));

            case LATER_THAN:
                return String.format("%s.isAfter(%s)", fieldName, formatDateValue(value));

            case EARLIER_THAN:
                return String.format("%s.isBefore(%s)", fieldName, formatDateValue(value));

            case RANGE:
                if (value.getClass().isArray()) {
                    return String.format("%s >= %s && %s <= %s",
                            fieldName, formatValue(Array.get(value, 0)),
                            fieldName, formatValue(Array.get(value, 1)));
                }
                throw new IllegalArgumentException("RANGE操作需要两个值");

            case IS_EMPTY:
                return String.format("(%s == null || %s.isEmpty())", fieldName, fieldName);

            case IS_NOT_EMPTY:
                return String.format("(%s != null && !%s.isEmpty())", fieldName, fieldName);

            case CONTAINS_ALL:
                return buildContainsAllExpression(fieldName, value);

            case NOT_CONTAINS_ALL:
                return String.format("!(%s)", buildContainsAllExpression(fieldName, value));

            case CONTAINS_ANY:
                return buildContainsAnyExpression(fieldName, value);

            case NOT_CONTAINS_ANY:
                return String.format("!(%s)", buildContainsAnyExpression(fieldName, value));

            default:
                throw new UnsupportedOperationException("不支持的操作符: " + operator);
        }
    }

    /**
     * 构建包含全部的表达式
     */
    private String buildContainsAllExpression(String fieldName, Object value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Array.getLength(value); i++) {
            if (i > 0) {
                sb.append(" && ");
            }
            sb.append(String.format("%s contains %s", fieldName, formatValue(Array.get(value, i))));
        }
        return sb.toString();
    }

    /**
     * 构建包含任一的表达式
     */
    private String buildContainsAnyExpression(String fieldName, Object value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Array.getLength(value); i++) {
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append(String.format("%s contains %s", fieldName, formatValue(Array.get(value, i))));
        }
        return sb.toString();
    }

    /**
     * 格式化值为表达式字符串
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "\\'") + "'";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Collection) {
            return formatCollectionValue((Collection<?>) value);
        }
        return "'" + value.toString() + "'";
    }

    /**
     * 格式化日期值
     */
    private String formatDateValue(Object value) {
        if (value instanceof LocalDate) {
            LocalDate date = (LocalDate) value;
            return String.format("LocalDate.of(%d, %d, %d)",
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            return String.format("LocalDateTime.of(%d, %d, %d, %d, %d, %d)",
                    dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
                    dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        }
        if (value instanceof LocalTime) {
            LocalTime time = (LocalTime) value;
            return String.format("LocalTime.of(%d, %d, %d)",
                    time.getHour(), time.getMinute(), time.getSecond());
        }
        return formatValue(value);
    }

    /**
     * 格式化集合值
     */
    private String formatCollectionValue(Collection<?> collection) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(formatValue(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

}
