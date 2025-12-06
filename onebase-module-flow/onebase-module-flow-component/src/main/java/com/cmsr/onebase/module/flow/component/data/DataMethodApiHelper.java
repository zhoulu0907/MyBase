package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.module.flow.context.condition.SortItem;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.express.AndExpression;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticCombinatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticSortDirectionEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据方法API工具类
 *
 * @Author：huangjie
 * @Date：2025/9/23 16:53
 */
public class DataMethodApiHelper {


    /**
     * 处理过滤条件
     *
     * @return 转换后的条件DTO列表
     */
    public static SemanticConditionDTO processFilterCondition(OrExpression orExpression) {
        // 条件不为空
        if (orExpression == null) {
            return null;
        }
        List<AndExpression> andExpressions = orExpression.getAndExpressions();
        if (CollectionUtils.isEmpty(andExpressions)) {
            return null;
        }
        // 仅有一个与条件
        if (andExpressions.size() == 1) {
            return convertAndExpression(andExpressions.get(0));
        }
        // 同时有多个条件
        List<SemanticConditionDTO> andSections = new ArrayList<>();
        for (AndExpression andExp : andExpressions) {
            SemanticConditionDTO andSemantic = convertAndExpression(andExp);
            if (andSemantic == null) {
                continue;
            }
            andSections.add(andSemantic);
        }
        // 若解析结果仍为空，则直接返回null
        if (andSections.isEmpty()) {
            return null;
        }
        // 返回结果仅有一个情况，不进行包装
        if (andSections.size() == 1) {
            return andSections.get(0);
        }
        SemanticConditionDTO rootConditions = new SemanticConditionDTO();
        rootConditions.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
        rootConditions.setCombinator(SemanticCombinatorEnum.OR);
        rootConditions.setChildren(andSections);
        return rootConditions;
    }

    public static SemanticConditionDTO convertAndExpression(AndExpression andExpression) {
        // 条件不能为空
        if (andExpression == null) {
            return null;
        }
        List<ExpressionItem> expressionItemList = andExpression.getExpressionItems();
        if (CollectionUtils.isEmpty(expressionItemList)) {
            return null;
        }
        // 仅包含一个条件，则不进行包装
        if (expressionItemList.size() == 1) {
            ExpressionItem expressionItem = expressionItemList.get(0);
            return convertExpressionItem(expressionItem);
        }
        // 同时包含多个条件
        List<SemanticConditionDTO> conditions = new ArrayList<>();
        for (ExpressionItem expItem : expressionItemList) {
            SemanticConditionDTO condition = convertExpressionItem(expItem);
            if (condition == null) {
                continue;
            }
            conditions.add(condition);
        }
        if (CollectionUtils.isEmpty(conditions)) {
            return null;
        }
        if (conditions.size() == 1) {
            return conditions.get(0);
        }
        SemanticConditionDTO rootConditions = new SemanticConditionDTO();
        rootConditions.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
        rootConditions.setCombinator(SemanticCombinatorEnum.AND);
        rootConditions.setChildren(conditions);
        return rootConditions;
    }

    public static SemanticConditionDTO convertExpressionItem(ExpressionItem expressionItem) {
        if (expressionItem == null) {
            return null;
        }
        String fieldName = expressionItem.getKey();
        OpEnum operator = expressionItem.getOp();
        Object itemValue = expressionItem.getValue();
        List<Object> dataArray = new ArrayList<>();
        // 保持最终的输出值是一个列表对象:
        //        obj => array<obj>
        // array<obj> => array<obj>
        if (itemValue instanceof List<?> itemArray) {
            dataArray.addAll(itemArray);
        } else {
            dataArray.add(itemValue);
        }
        SemanticConditionDTO condition = extractFromOperator(operator, dataArray);
        condition.setFieldName(fieldName);
        return condition;
    }

    public static SemanticConditionDTO extractFromOperator(OpEnum operator, List<?> valueArray) {
        SemanticConditionDTO condition = new SemanticConditionDTO();
        condition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
        List<Object> dataArray = new ArrayList<>(valueArray);
        condition.setFieldValue(dataArray);
        switch (operator) {
            case EQUALS -> { // K = V
                condition.setOperator(SemanticOperatorEnum.EQ);
            }
            case NOT_EQUALS -> { // K <> V
                condition.setOperator(SemanticOperatorEnum.NE);
            }
            case GREATER_THAN -> { // K > V
                condition.setOperator(SemanticOperatorEnum.GT);
            }
            case GREATER_EQUALS -> { // K >= V
                condition.setOperator(SemanticOperatorEnum.GE);
            }
            case LESS_THAN -> { // K < V
                condition.setOperator(SemanticOperatorEnum.LT);
            }
            case LESS_EQUALS -> { // K <= V
                condition.setOperator(SemanticOperatorEnum.LE);
            }
            case IS_EMPTY -> { // K IS NULL ;; K = NULL
                condition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
                condition.setOperator(SemanticOperatorEnum.EMPTY);
                condition.setFieldValue(null);
            }
            case IS_NOT_EMPTY -> { // K IS NOT NULL ;; K <> NULL
                condition.setOperator(SemanticOperatorEnum.NOT_EMPTY);
                condition.setFieldValue(null);
            }
            case EXISTS_IN -> { // K IN (V...)
                condition.setOperator(SemanticOperatorEnum.IN);
            }
            case NOT_EXISTS_IN -> { // K NOT IN (V...)
                condition.setOperator(SemanticOperatorEnum.NIN);
            }
            case LATER_THAN -> { // K<TIMESTAMP> > V
                condition.setOperator(SemanticOperatorEnum.GT);
            }
            case EARLIER_THAN -> { // K<TIMESTAMP> < V
                condition.setOperator(SemanticOperatorEnum.LT);
            }
            case CONTAINS -> { // K LIKE V
                condition.setOperator(SemanticOperatorEnum.LIKE);
            }
            case NOT_CONTAINS -> { // K NOT LIKE V
                condition.setOperator(SemanticOperatorEnum.NOT_LIKE);
            }
            case CONTAINS_ALL -> {
                condition.setOperator(SemanticOperatorEnum.CONTAINS_ALL);
            }
            case NOT_CONTAINS_ALL -> {
                condition.setOperator(SemanticOperatorEnum.NOT_CONTAINS_ALL);
            }
            case CONTAINS_ANY -> {
                condition.setOperator(SemanticOperatorEnum.CONTAINS_ANY);
            }
            case NOT_CONTAINS_ANY -> {
                condition.setOperator(SemanticOperatorEnum.NOT_CONTAINS_ANY);
            }
            case RANGE -> {
                if (valueArray.size() != 2) {
                    throw new IllegalArgumentException("范围条件仅接受2个参数，但获得到了" + valueArray.size() + "个," + valueArray);
                }
                condition.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
                condition.setCombinator(SemanticCombinatorEnum.AND);
                SemanticConditionDTO ltCondition = new SemanticConditionDTO();
                ltCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
                ltCondition.setOperator(SemanticOperatorEnum.LT);
                ltCondition.setFieldValue(List.of(valueArray.get(1)));
                SemanticConditionDTO gtCondition = new SemanticConditionDTO();
                gtCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
                gtCondition.setOperator(SemanticOperatorEnum.GT);
                gtCondition.setFieldValue(List.of(valueArray.get(0)));
                condition.setChildren(List.of(ltCondition, gtCondition));
            }
        }

        return condition;
    }


    /**
     * 处理排序条件
     */
    public static List<SemanticSortRuleDTO> processSortCondition(List<SortItem> sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return null;
        }
        List<SemanticSortRuleDTO> orderDtos = new ArrayList<>();
        // 遍历排序条件数组
        for (SortItem sortItem : sortBy) {
            if (sortItem.getSortField() == null || sortItem.getSortType() == null) {
                continue;
            }
            SemanticSortRuleDTO orderDto = new SemanticSortRuleDTO();
            // 设置排序字段ID
            orderDto.setField(sortItem.getSortField().toString());
            // 设置排序顺序
            orderDto.setDirection(SemanticSortDirectionEnum.valueOf(sortItem.getSortType()));
            orderDtos.add(orderDto);
        }
        return orderDtos.isEmpty() ? null : orderDtos;
    }


    public static Map<String, Object> convertToMap(SemanticEntityValueDTO fieldDataRespDTOS) {
        Map<String, Object> map = new HashMap<>();
        for (SemanticFieldValueDTO fieldDataRespDTO : fieldDataRespDTOS.getFieldValueMap().values()) {
            String key = fieldDataRespDTO.getFieldName();
            Object value = fieldDataRespDTO.getRawValue();
            map.put(key, value);
        }
        return map;
    }

    public static List<Map<String, Object>> convertToListMap(List<SemanticEntityValueDTO> fieldDataRespDTOSS) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SemanticEntityValueDTO fieldDataRespDTOS : fieldDataRespDTOSS) {
            Map<String, Object> map = convertToMap(fieldDataRespDTOS);
            list.add(map);
        }
        return list;
    }


}
