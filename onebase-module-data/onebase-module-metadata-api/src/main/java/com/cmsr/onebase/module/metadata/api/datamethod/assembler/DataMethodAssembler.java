package com.cmsr.onebase.module.metadata.api.datamethod.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.FieldData;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryCondition;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryOrder;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
import com.cmsr.onebase.module.metadata.core.domain.query.RowData;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.util.FieldValueUtil;

import jakarta.annotation.Resource;

/**
 * 数据方法转换器
 * 负责：
 * 1. DTO 与 领域模型之间的转换（原始职责）
 * 2. （合并自 Support）条件组装、字段ID->名称映射、核心结果包装为 QueryResult、基础校验与若干辅助工具。
 *
 * 说明：按当前需求合并 Support 逻辑，后续如需再次拆分，可将“非纯转换”逻辑再抽离为 Support/Helper。
 * 注意：部分方法含有对 core service 的依赖（字段元数据查询），已通过 @Resource 注入。
 *
 * 合并日期: 2025-09-25
 * 原作者: bty418
 * @author bty418
 * @date 2025-09-25
 */
@Component
public class DataMethodAssembler {

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

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
            queryRequest.setConditionGroups(convertConditionGroups(reqDTO.getConditionDTO()));
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

    // （已去重）原 toQueryConditionGroup 逻辑合并进 convertConditionGroups，避免两套实现维护成本

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

    // ============================= 以下为合并自 DataMethodSupport 的扩展能力 =============================

    /**
     * 基础请求校验（简单防御性校验，不进入业务规则）
     * @param reqDTO 请求参数
     */
    public void validateBase(EntityFieldDataReqDTO reqDTO) {
        if (reqDTO == null || reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
    }

    /**
     * 将二维条件 DTO 结构转换为领域条件结构（外层 OR，内层 AND）
     * @param groups 条件DTO分组
     * @return 领域条件分组
     */
    public List<List<QueryCondition>> convertConditionGroups(List<List<ConditionDTO>> groups) {
        if (groups == null) {
            return new ArrayList<>();
        }
        List<List<QueryCondition>> result = new ArrayList<>();
        for (List<ConditionDTO> inner : groups) {
            List<QueryCondition> oneGroup = new ArrayList<>();
            if (inner != null) {
                for (ConditionDTO dto : inner) {
                    QueryCondition qc = toQueryCondition(dto);
                    if (qc != null) {
                        oneGroup.add(qc);
                    }
                }
            }
            result.add(oneGroup);
        }
        return result;
    }

    /**
     * 供支持逻辑使用的 ConditionDTO -> QueryCondition（保留 public 以被 API 实现层复用）
     * @param dto 条件DTO
     * @return QueryCondition
     */
    public QueryCondition toQueryCondition(ConditionDTO dto) {
        if (dto == null) {
            return null;
        }
        QueryCondition qc = new QueryCondition();
        qc.setFieldId(dto.getFieldId());
        qc.setOperator(dto.getOperator());
        qc.setFieldValues(dto.getFieldValue());
        return qc;
    }

    /**
     * 字段ID Key -> 字段名称 Key 的 Map 转换
     * @param entityId 实体ID
     * @param idMap 以字段ID为 key 的 Map
     * @return 以字段名称为 key 的 Map
     */
    public Map<String, Object> convertIdKeyMapToNameKeyMap(Long entityId, Map<Long, Object> idMap) {
        if (idMap == null || idMap.isEmpty()) {
            return new HashMap<>();
        }
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
        Map<Long, String> idToName = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldName() != null)
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName, (a, b) -> a));
        Map<String, Object> result = new HashMap<>();
        idMap.forEach((k, v) -> {
            String name = idToName.get(k);
            if (name != null) {
                result.put(name, v);
            }
        });
        return result;
    }

    /**
     * 构建单条 core 返回 Map 的 QueryResult
     * @param entityId 实体ID
     * @param coreResult core 返回的数据结构 需要包含 key:data(Map), 可选 key:fieldType(Map)
     * @return QueryResult
     */
    @SuppressWarnings("unchecked")
    public QueryResult buildQueryResultFromCoreResult(Long entityId, Map<String, Object> coreResult) {
        QueryResult qr = new QueryResult();
        List<RowData> rows = new ArrayList<>();
        if (coreResult != null && coreResult.get("data") instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) coreResult.get("data");
            Map<String, String> fieldTypeMap = (Map<String, String>) coreResult.getOrDefault("fieldType", new HashMap<>());
            List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
            Map<String, MetadataEntityFieldDO> nameMap = fields.stream()
                    .filter(f -> f.getFieldName() != null)
                    .collect(Collectors.toMap(MetadataEntityFieldDO::getFieldName, f -> f, (a, b) -> a));

            List<FieldData> fieldDataList = new ArrayList<>();
            for (Map.Entry<String, Object> e : data.entrySet()) {
                FieldData fd = new FieldData();
                fd.setFieldName(e.getKey());
                fd.setFieldValue(e.getValue());
                MetadataEntityFieldDO fieldDO = nameMap.get(e.getKey());
                if (fieldDO != null) {
                    fd.setFieldId(fieldDO.getId());
                    fd.setDisplayName(fieldDO.getDisplayName());
                    fd.setFieldType(fieldDO.getFieldType());
                } else {
                    fd.setFieldType(fieldTypeMap.get(e.getKey()));
                }
                fieldDataList.add(fd);
            }
            RowData row = new RowData();
            row.setFieldDataList(fieldDataList);
            Object idVal = data.get("id");
            row.setRowId(idVal != null ? String.valueOf(idVal) : String.valueOf(data.hashCode()));
            rows.add(row);
        }
        qr.setRowDataList(rows);
        qr.setTotal((long) rows.size());
        return qr;
    }

    /**
     * 批量构建 QueryResult
     * @param entityId 实体ID
     * @param list core 多条记录列表
     * @return QueryResult
     */
    public QueryResult buildQueryResultFromCoreMultiResult(Long entityId, List<Map<String, Object>> list) {
        QueryResult qr = new QueryResult();
        List<RowData> rows = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> item : list) {
                rows.addAll(buildQueryResultFromCoreResult(entityId, item).getRowDataList());
            }
        }
        qr.setRowDataList(rows);
        qr.setTotal((long) rows.size());
        return qr;
    }

    /**
     * rowId 尝试解析为 Long，失败返回原字符串
     * @param rowId 行ID字符串
     * @return 解析后的 Long 或 原值
     */
    public Object tryParseId(String rowId) {
        if (rowId == null) {
            return null;
        }
        try {
            return Long.valueOf(rowId);
        } catch (NumberFormatException e) {
            return rowId;
        }
    }
}
