package com.cmsr.onebase.module.metadata.api.datamethod.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import com.cmsr.onebase.module.metadata.core.domain.query.FieldData;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryCondition;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryOrder;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
import com.cmsr.onebase.module.metadata.core.domain.query.RowData;
import com.cmsr.onebase.module.metadata.core.util.FieldValueUtil;

/**
 * 数据方法转换器
 * 负责DTO与领域模型之间的转换
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Component
public class DataMethodAssembler {

    /**
     * 将请求DTO转换为查询请求领域模型
     * 
     * @param reqDTO 请求DTO
     * @return 查询请求领域模型
     */
    public QueryRequest toQueryRequest(EntityFieldDataReqDTO reqDTO) {
        if (reqDTO == null) {
            return null;
        }

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setEntityId(reqDTO.getEntityId());
        queryRequest.setLimit(reqDTO.getNum());

        // 转换条件组
        if (!CollectionUtils.isEmpty(reqDTO.getConditionDTO())) {
            List<List<QueryCondition>> conditionGroups = reqDTO.getConditionDTO().stream()
                    .map(this::toQueryConditionGroup)
                    .collect(Collectors.toList());
            queryRequest.setConditionGroups(conditionGroups);
        }

        // 转换排序条件
        if (!CollectionUtils.isEmpty(reqDTO.getOrderDtos())) {
            List<QueryOrder> orders = reqDTO.getOrderDtos().stream()
                    .map(this::toQueryOrder)
                    .collect(Collectors.toList());
            queryRequest.setOrders(orders);
        }

        return queryRequest;
    }

    /**
     * 将条件DTO组转换为查询条件组
     * 
     * @param conditionDTOGroup 条件DTO组
     * @return 查询条件组
     */
    private List<QueryCondition> toQueryConditionGroup(List<ConditionDTO> conditionDTOGroup) {
        if (CollectionUtils.isEmpty(conditionDTOGroup)) {
            return new ArrayList<>();
        }

        return conditionDTOGroup.stream()
                .map(this::toQueryCondition)
                .collect(Collectors.toList());
    }

    /**
     * 将条件DTO转换为查询条件
     * 
     * @param conditionDTO 条件DTO
     * @return 查询条件
     */
    private QueryCondition toQueryCondition(ConditionDTO conditionDTO) {
        if (conditionDTO == null) {
            return null;
        }

        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setFieldId(conditionDTO.getFieldId());
        queryCondition.setOperator(conditionDTO.getOperator());
        queryCondition.setFieldValues(conditionDTO.getFieldValue());

        return queryCondition;
    }

    /**
     * 将排序DTO转换为查询排序
     * 
     * @param orderDto 排序DTO
     * @return 查询排序
     */
    private QueryOrder toQueryOrder(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }

        QueryOrder queryOrder = new QueryOrder();
        if (StringUtils.hasText(orderDto.getFieldId())) {
            try {
                queryOrder.setFieldId(Long.valueOf(orderDto.getFieldId()));
            } catch (NumberFormatException e) {
                // 忽略无效的字段ID
                return null;
            }
        }
        queryOrder.setDirection(StringUtils.hasText(orderDto.getSortOrder()) ? 
                               orderDto.getSortOrder().toUpperCase() : "ASC");

        return queryOrder;
    }

    /**
     * 将查询结果转换为响应DTO二维列表（按行组织）
     * 
     * @param queryResult 查询结果
     * @return 响应DTO二维列表（外层List表示多行，内层List表示一行的所有字段）
     */
    public List<List<EntityFieldDataRespDTO>> toResponseDTOs(QueryResult queryResult) {
        if (queryResult == null || CollectionUtils.isEmpty(queryResult.getRowDataList())) {
            return new ArrayList<>();
        }

        return queryResult.getRowDataList().stream()
                .map(this::toRowResponseDTOs)
                .collect(Collectors.toList());
    }

    /**
     * 将行数据转换为响应DTO列表（一行的所有字段）
     * 
     * @param rowData 行数据
     * @return 响应DTO列表（一行的所有字段）
     */
    private List<EntityFieldDataRespDTO> toRowResponseDTOs(RowData rowData) {
        if (rowData == null || CollectionUtils.isEmpty(rowData.getFieldDataList())) {
            return new ArrayList<>();
        }

        return rowData.getFieldDataList().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将字段数据转换为响应DTO
     * 
     * @param fieldData 字段数据
     * @return 响应DTO
     */
    private EntityFieldDataRespDTO toResponseDTO(FieldData fieldData) {
        if (fieldData == null) {
            return null;
        }

        EntityFieldDataRespDTO respDTO = new EntityFieldDataRespDTO();
        respDTO.setFieldId(fieldData.getFieldId());
        respDTO.setFieldName(fieldData.getFieldName());
        respDTO.setDisplayName(fieldData.getDisplayName());
        respDTO.setFieldType(fieldData.getFieldType());
        respDTO.setJdbcType(FieldValueUtil.inferJdbcType(fieldData.getFieldType()));
        respDTO.setFieldValue(fieldData.getFieldValue());

        return respDTO;
    }
}
