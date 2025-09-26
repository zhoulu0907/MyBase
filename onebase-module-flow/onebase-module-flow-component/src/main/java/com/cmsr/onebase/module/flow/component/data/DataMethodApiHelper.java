package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

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
@Component
public class DataMethodApiHelper {

    /**
     * 将Map数据转换为EntityFieldDataReqDTO对象
     *
     * @param data 包含查询条件的Map数据
     * @return 转换后的EntityFieldDataReqDTO对象
     */
    public EntityFieldDataReqDTO convertQueryReq(Map<String, Object> data, VariableContext variableContext) {
        EntityFieldDataReqDTO reqDTO = new EntityFieldDataReqDTO();

        // 设置实体ID
        reqDTO.setEntityId(MapUtils.getLong(data, "mainEntityId"));
        if (reqDTO.getEntityId() == null) {
            reqDTO.setEntityId(MapUtils.getLong(data, "subEntityId"));
        }

        // 处理过滤条件
        List<List<ConditionDTO>> conditionDTOList = processFilterCondition(data, variableContext);
        if (conditionDTOList != null && !conditionDTOList.isEmpty()) {
            reqDTO.setConditionDTO(conditionDTOList);
        }

        // 处理排序条件
        List<OrderDto> orderDtos = processSortCondition(data);
        if (orderDtos != null && !orderDtos.isEmpty()) {
            reqDTO.setOrderDtos(orderDtos);
        }

        return reqDTO;
    }

    /**
     * 处理过滤条件
     *
     * @param data 包含查询条件的Map数据
     * @return 转换后的条件DTO列表
     */
    private List<List<ConditionDTO>> processFilterCondition(Map<String, Object> data, VariableContext variableContext) {
        List<Map<String, Object>> filterCondition = (List<Map<String, Object>>) MapUtils.getObject(data, "filterCondition");
        if (filterCondition == null || filterCondition.isEmpty()) {
            return null;
        }

        List<List<ConditionDTO>> conditionDTOList = new ArrayList<>();

        // 遍历外层filterCondition数组（OR关系）
        for (Map<String, Object> outerCondition : filterCondition) {
            List<Map<String, Object>> innerConditions = (List<Map<String, Object>>) MapUtils.getObject(outerCondition, "conditions");
            if (innerConditions != null && !innerConditions.isEmpty()) {
                List<ConditionDTO> innerConditionDTOList = new ArrayList<>();

                // 遍历内层conditions数组（AND关系）
                for (Map<String, Object> innerCondition : innerConditions) {
                    ConditionDTO conditionDTO = new ConditionDTO();

                    // 设置字段ID
                    conditionDTO.setFieldId(MapUtils.getLong(innerCondition, "fieldId"));
                    // 设置操作符
                    conditionDTO.setOperator(MapUtils.getString(innerCondition, "op"));

                    String operatorType = MapUtils.getString(innerCondition, "operatorType");
                    Object value = MapUtils.getObject(innerCondition, "value");
                    value = convertValue(operatorType, value, variableContext);
                    // 设置字段值

                    if (value != null) {
                        List<String> fieldValueList = new ArrayList<>();
                        fieldValueList.add(value.toString());
                        conditionDTO.setFieldValue(fieldValueList);
                    }
                    innerConditionDTOList.add(conditionDTO);
                }

                if (!innerConditionDTOList.isEmpty()) {
                    conditionDTOList.add(innerConditionDTOList);
                }
            }
        }

        return conditionDTOList.isEmpty() ? null : conditionDTOList;
    }

    public Object convertValue(String operatorType, Object value, VariableContext variableContext) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(operatorType);
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            return value;
        }
        if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            return variableContext.getVariableByExpression(value.toString());
        }
        return value;
    }

    public Object convertValue(int i, String operatorType, Object value, VariableContext variableContext) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(operatorType);
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            return value;
        }
        if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            String expression = value.toString();
            return variableContext.getVariableByExpression(i, expression);
        }
        return value;
    }

    /**
     * 处理排序条件
     *
     * @param data 包含查询条件的Map数据
     * @return 转换后的排序DTO列表
     */
    private List<OrderDto> processSortCondition(Map<String, Object> data) {
        List<Map<String, Object>> sortBy = (List<Map<String, Object>>) MapUtils.getObject(data, "sortBy");
        if (sortBy == null || sortBy.isEmpty()) {
            return null;
        }

        List<OrderDto> orderDtos = new ArrayList<>();

        // 遍历排序条件数组
        for (Map<String, Object> sortItem : sortBy) {
            OrderDto orderDto = new OrderDto();

            // 设置排序字段ID
            orderDto.setFieldId(MapUtils.getString(sortItem, "sortField"));
            // 设置排序顺序
            orderDto.setSortOrder(MapUtils.getString(sortItem, "sortType"));

            // 只有当排序字段ID和排序顺序都不为空时才添加
            if (orderDto.getFieldId() != null && orderDto.getSortOrder() != null) {
                orderDtos.add(orderDto);
            }
        }
        return orderDtos.isEmpty() ? null : orderDtos;
    }


    public Map<String, Object> convertToMap(List<EntityFieldDataRespDTO> fieldDataRespDTOS) {
        Map<String, Object> map = new HashMap<>();
        for (EntityFieldDataRespDTO fieldDataRespDTO : fieldDataRespDTOS) {
            String key = String.valueOf(fieldDataRespDTO.getFieldId());
            Object value = JdbcTypeConvertor.convert(fieldDataRespDTO.getJdbcType(), fieldDataRespDTO.getFieldValue());
            map.put(key, value);
        }
        return map;
    }

    public List<Map<String, Object>> convertToListMap(List<List<EntityFieldDataRespDTO>> fieldDataRespDTOSS) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (List<EntityFieldDataRespDTO> fieldDataRespDTOS : fieldDataRespDTOSS) {
            Map<String, Object> map = convertToMap(fieldDataRespDTOS);
            list.add(map);
        }
        return list;
    }


}
