package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.condition.SortItem;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import org.apache.commons.lang3.math.NumberUtils;

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
    public static List<List<ConditionDTO>> processFilterCondition(List<ConditionItem> conditionItems) {
        List<List<ConditionDTO>> conditionDTtoSS = new ArrayList<>();
        for (ConditionItem conditionItem : conditionItems) {
            List<ConditionDTO> conditionDtoS = new ArrayList<>();
            for (RuleItem ruleItem : conditionItem.getRules()) {
                ConditionDTO conditionDTO = new ConditionDTO();
                conditionDTO.setFieldId(NumberUtils.toLong(ruleItem.getFieldId()));
                conditionDTO.setOperator(ruleItem.getOp());
                if (ruleItem.getValue() == null) {
                    conditionDTO.setFieldValue(null);
                } else if (ruleItem.getValue() instanceof List l) {
                    conditionDTO.setFieldValue(l);
                } else {
                    conditionDTO.setFieldValue(List.of(ruleItem.getValue().toString()));
                }
                conditionDtoS.add(conditionDTO);
            }
            conditionDTtoSS.add(conditionDtoS);
        }
        return conditionDTtoSS;
    }


    /**
     * 处理排序条件
     */
    public static List<OrderDto> processSortCondition(List<SortItem> sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return null;
        }
        List<OrderDto> orderDtos = new ArrayList<>();
        // 遍历排序条件数组
        for (SortItem sortItem : sortBy) {
            if (sortItem.getSortField() == null || sortItem.getSortType() == null) {
                continue;
            }
            OrderDto orderDto = new OrderDto();
            // 设置排序字段ID
            orderDto.setFieldId(sortItem.getSortField().toString());
            // 设置排序顺序
            orderDto.setSortOrder(sortItem.getSortType());
            orderDtos.add(orderDto);
        }
        return orderDtos.isEmpty() ? null : orderDtos;
    }


    public static Map<String, Object> convertToMap(List<EntityFieldDataRespDTO> fieldDataRespDTOS) {
        Map<String, Object> map = new HashMap<>();
        for (EntityFieldDataRespDTO fieldDataRespDTO : fieldDataRespDTOS) {
            String key = String.valueOf(fieldDataRespDTO.getFieldId());
            Object value = JdbcTypeConvertor.convert(fieldDataRespDTO.getJdbcType(), fieldDataRespDTO.getFieldValue());
            map.put(key, value);
        }
        return map;
    }

    public static List<Map<String, Object>> convertToListMap(List<List<EntityFieldDataRespDTO>> fieldDataRespDTOSS) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (List<EntityFieldDataRespDTO> fieldDataRespDTOS : fieldDataRespDTOSS) {
            Map<String, Object> map = convertToMap(fieldDataRespDTOS);
            list.add(map);
        }
        return list;
    }


}
