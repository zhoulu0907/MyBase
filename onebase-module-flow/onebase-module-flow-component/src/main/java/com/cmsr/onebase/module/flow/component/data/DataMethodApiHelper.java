package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
@Setter
@Component
public class DataMethodApiHelper {

    /**
     * 将Map数据转换为EntityFieldDataReqDTO对象
     *
     * @param nodeData 包含查询条件的Map数据
     * @return 转换后的EntityFieldDataReqDTO对象
     */
    public EntityFieldDataReqDTO convertQueryReq(NodeData nodeData, List<ConditionItem> conditionItems) {
        EntityFieldDataReqDTO reqDTO = new EntityFieldDataReqDTO();

        // 设置实体ID
        reqDTO.setEntityId(nodeData.getLong("mainEntityId"));
        if (reqDTO.getEntityId() == null) {
            reqDTO.setEntityId(nodeData.getLong("subEntityId"));
        }

        // 处理过滤条件
        List<List<ConditionDTO>> conditionDTOList = processFilterCondition(conditionItems);
        if (conditionDTOList != null && !conditionDTOList.isEmpty()) {
            reqDTO.setConditionDTO(conditionDTOList);
        }

        // 处理排序条件
        List<OrderDto> orderDtos = processSortCondition(nodeData);
        if (orderDtos != null && !orderDtos.isEmpty()) {
            reqDTO.setOrderDtos(orderDtos);
        }

        return reqDTO;
    }

    /**
     * 处理过滤条件
     *
     * @return 转换后的条件DTO列表
     */
    public List<List<ConditionDTO>> processFilterCondition(List<ConditionItem> conditionItems) {
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
     *
     * @param nodeData 包含查询条件的Map数据
     * @return 转换后的排序DTO列表
     */
    private List<OrderDto> processSortCondition(NodeData nodeData) {
        List<Map<String, Object>> sortBy = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "sortBy");
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
