// package com.cmsr.onebase.module.metadata.api.datamethod.assembler;

// import java.sql.Date;
// import java.sql.Timestamp;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Component;
// import org.springframework.util.CollectionUtils;
// import org.springframework.util.StringUtils;

// import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
// import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
// import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
// import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
// import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
// import com.cmsr.onebase.module.metadata.core.domain.query.FieldData;
// import com.cmsr.onebase.module.metadata.core.domain.query.QueryCondition;
// import com.cmsr.onebase.module.metadata.core.domain.query.QueryOrder;
// import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
// import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
// import com.cmsr.onebase.module.metadata.core.domain.query.RowData;
// import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
// import com.cmsr.onebase.module.metadata.core.util.FieldValueUtil;

// import jakarta.annotation.Resource;

// /**
//  * 数据方法转换器（运行态）
//  * 负责：
//  * 1. DTO 与 领域模型之间的转换
//  * 2. 条件组装、字段ID->名称映射、核心结果包装为 QueryResult、基础校验与若干辅助工具
//  *
//  * @author bty418
//  * @date 2025-10-23
//  */
// @Component
// public class DataMethodAssembler {

//     @Resource
//     private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

//     /**
//      * 将请求DTO转换为查询请求领域模型
//      * 
//      * @param reqDTO 请求DTO
//      * @return 查询请求领域模型
//      */
//     public QueryRequest toQueryRequest(EntityFieldDataReqDTO reqDTO) {
//         if (reqDTO == null) {
//             return null;
//         }

//         QueryRequest queryRequest = new QueryRequest();
//         queryRequest.setEntityId(reqDTO.getEntityId());
//         queryRequest.setLimit(reqDTO.getNum());

//         // 转换全局 AND 条件
//         if (!CollectionUtils.isEmpty(reqDTO.getAndConditionDTO())) {
//             List<QueryCondition> andConditions = reqDTO.getAndConditionDTO().stream()
//                     .map(this::toQueryCondition)
//                     .filter(qc -> qc != null)
//                     .collect(Collectors.toList());
//             queryRequest.setAndConditions(andConditions);
//         }

//         // 转换条件组
//         if (!CollectionUtils.isEmpty(reqDTO.getConditionDTO())) {
//             queryRequest.setConditionGroups(convertConditionGroups(reqDTO.getConditionDTO()));
//         }

//         // 转换排序条件
//         if (!CollectionUtils.isEmpty(reqDTO.getOrderDtos())) {
//             List<QueryOrder> orders = reqDTO.getOrderDtos().stream()
//                     .map(this::toQueryOrder)
//                     .collect(Collectors.toList());
//             queryRequest.setOrders(orders);
//         }

//         return queryRequest;
//     }

//     /**
//      * 将排序DTO转换为查询排序
//      * 
//      * @param orderDto 排序DTO
//      * @return 查询排序
//      */
//     private QueryOrder toQueryOrder(OrderDto orderDto) {
//         if (orderDto == null) {
//             return null;
//         }

//         QueryOrder queryOrder = new QueryOrder();
//         if (StringUtils.hasText(orderDto.getFieldId())) {
//             try {
//                 queryOrder.setFieldId(Long.valueOf(orderDto.getFieldId()));
//             } catch (NumberFormatException e) {
//                 // 忽略无效的字段ID
//                 return null;
//             }
//         }
//         queryOrder.setDirection(StringUtils.hasText(orderDto.getSortOrder()) ? 
//                                orderDto.getSortOrder().toUpperCase() : "ASC");

//         return queryOrder;
//     }

//     /**
//      * 将查询结果转换为响应DTO二维列表（按行组织）
//      * 
//      * @param queryResult 查询结果
//      * @return 响应DTO二维列表（外层List表示多行，内层List表示一行的所有字段）
//      */
//     public List<List<EntityFieldDataRespDTO>> toResponseDTOs(QueryResult queryResult) {
//         if (queryResult == null || CollectionUtils.isEmpty(queryResult.getRowDataList())) {
//             return new ArrayList<>();
//         }

//         return queryResult.getRowDataList().stream()
//                 .map(this::toRowResponseDTOs)
//                 .collect(Collectors.toList());
//     }

//     /**
//      * 将行数据转换为响应DTO列表（一行的所有字段）
//      * 
//      * @param rowData 行数据
//      * @return 响应DTO列表（一行的所有字段）
//      */
//     private List<EntityFieldDataRespDTO> toRowResponseDTOs(RowData rowData) {
//         if (rowData == null || CollectionUtils.isEmpty(rowData.getFieldDataList())) {
//             return new ArrayList<>();
//         }

//         return rowData.getFieldDataList().stream()
//                 .map(this::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     /**
//      * 将字段数据转换为响应DTO
//      * 
//      * @param fieldData 字段数据
//      * @return 响应DTO
//      */
//     private EntityFieldDataRespDTO toResponseDTO(FieldData fieldData) {
//         if (fieldData == null) {
//             return null;
//         }

//         EntityFieldDataRespDTO respDTO = new EntityFieldDataRespDTO();
//         respDTO.setFieldId(fieldData.getFieldId());
//         respDTO.setFieldName(fieldData.getFieldName());
//         respDTO.setDisplayName(fieldData.getDisplayName());
//         respDTO.setFieldType(fieldData.getFieldType());
//         respDTO.setJdbcType(FieldValueUtil.inferJdbcType(fieldData.getFieldType()));
//         respDTO.setFieldValue(fieldData.getFieldValue());

//         return respDTO;
//     }

//     /**
//      * 基础请求校验（简单防御性校验，不进入业务规则）
//      *
//      * @param reqDTO 请求参数
//      */
//     public void validateBase(EntityFieldDataReqDTO reqDTO) {
//         if (reqDTO == null || reqDTO.getEntityId() == null) {
//             throw new IllegalArgumentException("实体ID不能为空");
//         }
//     }

//     /**
//      * 将二维条件 DTO 结构转换为领域条件结构（外层 OR，内层 AND）
//      *
//      * @param groups 条件DTO分组
//      * @return 领域条件分组
//      */
//     public List<List<QueryCondition>> convertConditionGroups(List<List<ConditionDTO>> groups) {
//         if (groups == null) {
//             return new ArrayList<>();
//         }
//         List<List<QueryCondition>> result = new ArrayList<>();
//         for (List<ConditionDTO> inner : groups) {
//             List<QueryCondition> oneGroup = new ArrayList<>();
//             if (inner != null) {
//                 for (ConditionDTO dto : inner) {
//                     QueryCondition qc = toQueryCondition(dto);
//                     if (qc != null) {
//                         oneGroup.add(qc);
//                     }
//                 }
//             }
//             result.add(oneGroup);
//         }
//         return result;
//     }

//     /**
//      * ConditionDTO -> QueryCondition
//      *
//      * @param dto 条件DTO
//      * @return QueryCondition
//      */
//     public QueryCondition toQueryCondition(ConditionDTO dto) {
//         if (dto == null) {
//             return null;
//         }
//         QueryCondition qc = new QueryCondition();

//         // 当用户输入的条件值为 空字符串/“null”/未设置任何值，将值修改未NULL，否则AnyLine忽略处理【AnyLine会将NULL对应的条件改为 IS NULL】
//         List<String> values = dto.getFieldValue();
//         if(values == null){
//             String newValue = "NULL";
//             List newList = new ArrayList<String>();
//             newList.add(newValue);
//             qc.setFieldId(dto.getFieldId());
//             qc.setOperator(dto.getOperator());
//             qc.setFieldValues(newList);
//             return qc;
//         }
//         if(values.size() == 1){
//             String value = values.get(0);
//             if (!StringUtils.hasText(value) || "null".equals(value)){
//                 String newValue = "NULL";
//                 List newList = new ArrayList<String>();
//                 newList.add(newValue);
//                 qc.setFieldId(dto.getFieldId());
//                 qc.setOperator(dto.getOperator());
//                 qc.setFieldValues(newList);
//                 return qc;
//             }
//         }

//         qc.setFieldId(dto.getFieldId());
//         qc.setOperator(dto.getOperator());
//         qc.setFieldValues(dto.getFieldValue());
//         return qc;
//     }

//     /**
//      * 字段ID Key -> 字段名称 Key 的 Map 转换
//      *
//      * @param entityId 实体ID
//      * @param idMap 以字段ID为 key 的 Map
//      * @return 以字段名称为 key 的 Map
//      */
//     public Map<String, Object> convertIdKeyMapToNameKeyMap(Long entityId, Map<Long, Object> idMap) {
//         if (idMap == null || idMap.isEmpty()) {
//             return new HashMap<>();
//         }
//         List<Map<String, Object>> dataList = convertIdKeyMapListToNameKeyMapList(entityId, Collections.singletonList(idMap));
//         return dataList.isEmpty() ? new HashMap<>() : dataList.get(0);
//     }

//     /**
//      * 批量将字段ID键的Map列表转换为字段名称键的Map列表，并按照字段类型进行值转换
//      *
//      * @param entityId 实体ID
//      * @param idMapList 字段ID键的Map列表
//      * @return 字段名称键的Map列表
//      */
//     public List<Map<String, Object>> convertIdKeyMapListToNameKeyMapList(Long entityId, List<Map<Long, Object>> idMapList) {
//         if (CollectionUtils.isEmpty(idMapList)) {
//             return new ArrayList<>();
//         }
//         List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
//         Map<Long, MetadataEntityFieldDO> idToField = fields.stream()
//                 .filter(f -> f.getId() != null && StringUtils.hasText(f.getFieldName()))
//                 .collect(Collectors.toMap(MetadataEntityFieldDO::getId, field -> field, (a, b) -> a));

//         List<Map<String, Object>> resultList = new ArrayList<>(idMapList.size());
//         for (Map<Long, Object> idMap : idMapList) {
//             resultList.add(convertSingleIdMap(idMap, idToField));
//         }
//         return resultList;
//     }

//     /**
//      * 单个ID Map转换
//      *
//      * @param idMap 字段ID为key的Map
//      * @param idToField 字段ID到字段DO的映射
//      * @return 字段名称为key的Map
//      */
//     private Map<String, Object> convertSingleIdMap(Map<Long, Object> idMap, Map<Long, MetadataEntityFieldDO> idToField) {
//         if (idMap == null || idMap.isEmpty()) {
//             return new HashMap<>();
//         }
//         Map<String, Object> result = new LinkedHashMap<>();
//         idMap.forEach((fieldId, value) -> {
//             MetadataEntityFieldDO field = idToField.get(fieldId);
//             if (field == null || !StringUtils.hasText(field.getFieldName())) {
//                 return;
//             }
//             result.put(field.getFieldName(), convertValueByField(value, field));
//         });
//         return result;
//     }

//     /**
//      * 按字段类型转换值
//      *
//      * @param value 原始值
//      * @param field 字段定义
//      * @return 转换后的值
//      */
//     private Object convertValueByField(Object value, MetadataEntityFieldDO field) {
//         if (value == null) {
//             return null;
//         }
//         if (value instanceof String) {
//             Object converted = FieldValueUtil.convertFieldValue((String) value, field);
//             return adaptJdbcTemporal(converted, field);
//         }
//         if (value instanceof List<?>) {
//             List<?> rawList = (List<?>) value;
//             boolean convertible = rawList.stream().allMatch(item -> item == null || item instanceof String);
//             if (convertible) {
//                 return rawList.stream()
//                         .map(item -> {
//                             if (item == null) {
//                                 return null;
//                             }
//                             Object converted = FieldValueUtil.convertFieldValue((String) item, field);
//                             return adaptJdbcTemporal(converted, field);
//                         })
//                         .collect(Collectors.toList());
//             }
//         }
//         return adaptJdbcTemporal(value, field);
//     }

//     /**
//      * 适配JDBC时间类型
//      *
//      * @param value 值
//      * @param field 字段定义
//      * @return 适配后的值
//      */
//     private Object adaptJdbcTemporal(Object value, MetadataEntityFieldDO field) {
//         if (value == null || field == null || !StringUtils.hasText(field.getFieldType())) {
//             return value;
//         }
//         return adaptJdbcTemporal(value, field.getFieldType());
//     }

//     /**
//      * 适配JDBC时间类型
//      *
//      * @param value 值
//      * @param fieldType 字段类型
//      * @return 适配后的值
//      */
//     private Object adaptJdbcTemporal(Object value, String fieldType) {
//         if (value == null || !StringUtils.hasText(fieldType)) {
//             return value;
//         }
//         String upperType = fieldType.toUpperCase();
//         switch (upperType) {
//             case "DATETIME":
//             case "TIMESTAMP":
//                 if (value instanceof LocalDateTime) {
//                     return Timestamp.valueOf((LocalDateTime) value);
//                 }
//                 return value;
//             case "DATE":
//                 if (value instanceof LocalDate) {
//                     return Date.valueOf((LocalDate) value);
//                 }
//                 return value;
//             default:
//                 return value;
//         }
//     }

//     /**
//      * 构建单条 core 返回 Map 的 QueryResult
//      *
//      * @param entityId 实体ID
//      * @param coreResult core 返回的数据结构 需要包含 key:data(Map), 可选 key:fieldType(Map)
//      * @return QueryResult
//      */
//     @SuppressWarnings("unchecked")
//     public QueryResult buildQueryResultFromCoreResult(Long entityId, Map<String, Object> coreResult) {
//         QueryResult qr = new QueryResult();
//         List<RowData> rows = new ArrayList<>();
//         if (coreResult != null && coreResult.get("data") instanceof Map) {
//             Map<String, Object> data = (Map<String, Object>) coreResult.get("data");
//             Map<String, String> fieldTypeMap = (Map<String, String>) coreResult.getOrDefault("fieldType", new HashMap<>());
//             List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
//             Map<String, MetadataEntityFieldDO> nameMap = fields.stream()
//                     .filter(f -> f.getFieldName() != null)
//                     .collect(Collectors.toMap(MetadataEntityFieldDO::getFieldName, f -> f, (a, b) -> a));

//             List<FieldData> fieldDataList = new ArrayList<>();
//             for (Map.Entry<String, Object> e : data.entrySet()) {
//                 FieldData fd = new FieldData();
//                 fd.setFieldName(e.getKey());
//                 MetadataEntityFieldDO fieldDO = nameMap.get(e.getKey());
//                 if (fieldDO != null) {
//                     fd.setFieldId(fieldDO.getId());
//                     fd.setDisplayName(fieldDO.getDisplayName());
//                     fd.setFieldType(fieldDO.getFieldType());
//                     fd.setFieldValue(adaptJdbcTemporal(e.getValue(), fieldDO));
//                 } else {
//                     String fieldType = fieldTypeMap.get(e.getKey());
//                     fd.setFieldType(fieldType);
//                     fd.setFieldValue(adaptJdbcTemporal(e.getValue(), fieldType));
//                 }
//                 fieldDataList.add(fd);
//             }
//             RowData row = new RowData();
//             row.setFieldDataList(fieldDataList);
//             Object idVal = data.get("id");
//             row.setRowId(idVal != null ? String.valueOf(idVal) : String.valueOf(data.hashCode()));
//             rows.add(row);
//         }
//         qr.setRowDataList(rows);
//         qr.setTotal((long) rows.size());
//         return qr;
//     }

//     /**
//      * 批量构建 QueryResult
//      *
//      * @param entityId 实体ID
//      * @param list core 多条记录列表
//      * @return QueryResult
//      */
//     public QueryResult buildQueryResultFromCoreMultiResult(Long entityId, List<Map<String, Object>> list) {
//         QueryResult qr = new QueryResult();
//         List<RowData> rows = new ArrayList<>();
//         if (list != null) {
//             for (Map<String, Object> item : list) {
//                 rows.addAll(buildQueryResultFromCoreResult(entityId, item).getRowDataList());
//             }
//         }
//         qr.setRowDataList(rows);
//         qr.setTotal((long) rows.size());
//         return qr;
//     }

//     /**
//      * rowId 尝试解析为 Long，失败返回原字符串
//      *
//      * @param rowId 行ID字符串
//      * @return 解析后的 Long 或 原值
//      */
//     public Object tryParseId(String rowId) {
//         if (rowId == null) {
//             return null;
//         }
//         try {
//             return Long.valueOf(rowId);
//         } catch (NumberFormatException e) {
//             return rowId;
//         }
//     }
// }

