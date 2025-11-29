package com.cmsr.onebase.module.metadata.runtime.semantic.adapter;

import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.util.EntityFieldDataConverter;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataCreateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataGetReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataPageReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataDeleteReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataUpdateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
/**
 * RecordDTO -> 运行态数据方法请求VO 装配器
 *
 * <p>负责将语义化的 RecordDTO 转换为各类运行态数据方法请求对象，
 * 包括创建/更新/删除/详情/分页，并构造子表数据结构。</p>
 */
public class SemanticRecordAssembler {

    @Resource
    private MetadataEntityFieldCoreService fieldCoreService;

    /**
     * 装配创建请求
     * @param record 记录承载对象
     * @return 创建请求VO
     */
    public DynamicDataCreateReqVO toCreateReq(SemanticRecordDTO record) {
        DynamicDataCreateReqVO req = new DynamicDataCreateReqVO();
        if (record == null) {
            return req;
        }
        if (record.getRecordContext() != null) {
            req.setTraceId(record.getRecordContext().getTraceId());
            req.setMenuId(record.getRecordContext().getMenuId());
            req.setMethodCode(codeOf(record.getRecordContext().getMethodCode()));
        }
        Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
        req.setEntityId(entityId);
        Map<String, SemanticFieldValueDTO<Object>> dtoMap = record.getEntityValue() != null ? record.getEntityValue().getFieldValueMap() : null;
        Map<String, Object> nameMap = toNameValueMap(dtoMap);
        Map<Long, Object> idMap = nameMap == null ? new HashMap<>() : EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(entityId, nameMap, fieldCoreService);
        req.setData(idMap);
        req.setSubEntities(buildSubEntities(record));
        return req;
    }

    /**
     * 装配更新请求
     * @param record 记录承载对象
     * @return 更新请求VO
     */
    public DynamicDataUpdateReqVO toUpdateReq(SemanticRecordDTO record) {
        DynamicDataUpdateReqVO req = new DynamicDataUpdateReqVO();
        if (record == null) {
            return req;
        }
        if (record.getRecordContext() != null) {
            req.setMenuId(record.getRecordContext().getMenuId());
            req.setMethodCode(codeOf(record.getRecordContext().getMethodCode()));
        }
        Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getEntityValue() != null && record.getEntityValue().getFieldValueMap() != null ? record.getEntityValue().getFieldValueMap().get("id") : null;
        req.setId(id);
        Map<String, SemanticFieldValueDTO<Object>> dtoMap = record.getEntityValue() != null ? record.getEntityValue().getFieldValueMap() : null;
        Map<String, Object> nameMap = toNameValueMap(dtoMap);
        Map<Long, Object> idMap = nameMap == null ? new HashMap<>() : EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(entityId, nameMap, fieldCoreService);
        req.setData(idMap);
        req.setSubEntities(buildSubEntities(record));
        return req;
    }

    /**
     * 装配删除请求
     * @param record 记录承载对象
     * @return 删除请求VO
     */
    public DynamicDataDeleteReqVO toDeleteReq(SemanticRecordDTO record) {
        DynamicDataDeleteReqVO req = new DynamicDataDeleteReqVO();
        if (record == null) {
            return req;
        }
        if (record.getRecordContext() != null) {
            req.setMenuId(record.getRecordContext().getMenuId());
            req.setMethodCode(codeOf(record.getRecordContext().getMethodCode()));
        }
        Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getEntityValue() != null && record.getEntityValue().getFieldValueMap() != null ?
                (record.getEntityValue().getFieldValueMap().get("id") == null ? null : record.getEntityValue().getFieldValueMap().get("id").getRawValue()) : null;
        req.setId(id == null ? null : Long.valueOf(String.valueOf(id)));
        return req;
    }

    /**
     * 装配详情查询请求
     * @param record 记录承载对象
     * @return 查询请求VO
     */
    public DynamicDataGetReqVO toGetReq(SemanticRecordDTO record) {
        DynamicDataGetReqVO req = new DynamicDataGetReqVO();
        if (record == null) {
            return req;
        }
        if (record.getRecordContext() != null) {
            req.setMenuId(record.getRecordContext().getMenuId());
            req.setMethodCode(codeOf(record.getRecordContext().getMethodCode()));
        }
        Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getEntityValue() != null && record.getEntityValue().getFieldValueMap() != null ?
                (record.getEntityValue().getFieldValueMap().get("id") == null ? null : record.getEntityValue().getFieldValueMap().get("id").getRawValue()) : null;
        req.setId(id == null ? null : Long.valueOf(String.valueOf(id)));
        return req;
    }

    /**
     * 装配分页查询请求
     * @param record 记录承载对象
     * @return 分页请求VO
     */
    public DynamicDataPageReqVO toPageReq(SemanticRecordDTO record) {
        DynamicDataPageReqVO req = new DynamicDataPageReqVO();
        if (record == null) {
            return req;
        }
        if (record.getRecordContext() != null) {
            req.setMenuId(record.getRecordContext().getMenuId());
            req.setPageNo(record.getRecordContext().getPageNo());
            req.setPageSize(record.getRecordContext().getPageSize());
            if (record.getRecordContext().getSortBy() != null && !record.getRecordContext().getSortBy().isEmpty()) {
                var first = record.getRecordContext().getSortBy().get(0);
                req.setSortField(first.getField());
                req.setSortDirection(first.getDirection() == null ? null : first.getDirection().name());
            }
            Map<String, Object> nameFilters = record.getRecordContext().getFilters();
            Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
            Map<Long, Object> idFilters = nameFilters == null ? null :
                    EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(entityId, nameFilters, fieldCoreService);
            req.setFilters(idFilters);
            req.setMethodCode(codeOf(record.getRecordContext().getMethodCode()));
        }
        Long entityId = record.getEntitySchema() != null ? record.getEntitySchema().getId() : null;
        req.setEntityId(entityId);
        return req;
    }

    /**
     * 构造子表数据列表：按连接器模型将行值转换为目标实体的 idKey Map
     * @param record 记录承载对象
     * @return 子表数据列表
     */
    private List<SubEntityVo> buildSubEntities(SemanticRecordDTO record) {
        List<SubEntityVo> list = new ArrayList<>();
        if (record == null || record.getEntityValue() == null || record.getEntityValue().getConnectors() == null) {
            return list;
        }
        Map<String, SemanticRelationValueDTO> connectors = record.getEntityValue().getConnectors();
        List<SemanticRelationSchemaDTO> schemas = record.getEntitySchema() != null ? record.getEntitySchema().getConnectors() : null;
        Map<String, SemanticRelationSchemaDTO> schemaMap = new HashMap<>();
        if (schemas != null) {
            for (SemanticRelationSchemaDTO s : schemas) {
                if (s != null && s.getName() != null) {
                    schemaMap.put(s.getName(), s);
                }
            }
        }
        for (Map.Entry<String, SemanticRelationValueDTO> entry : connectors.entrySet()) {
            String name = entry.getKey();
            SemanticRelationValueDTO v = entry.getValue();
            SemanticRelationSchemaDTO s = schemaMap.get(name);
            if (s == null) {
                int pos = name.indexOf('_');
                if (pos >= 0 && pos + 1 < name.length()) {
                    String trimmed = name.substring(pos + 1);
                    s = schemaMap.get(trimmed);
                }
                if (s == null) {
                    continue;
                }
            }
            Long targetEntityId = s.getTargetEntityId();
            List<Map<Long, Object>> subDataList = new ArrayList<>();
            if (v.getRowValue() != null) {
                SemanticRowValueDTO row = v.getRowValue();
                Map<String, Object> nameValueMap = toNameValueMap(row.getFields());
                Map<Long, Object> idKeyMap = EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(targetEntityId, nameValueMap, fieldCoreService);
                subDataList.add(idKeyMap);
            }
            if (v.getRowValueList() != null) {
                for (SemanticRowValueDTO row : v.getRowValueList()) {
                    Map<String, Object> nameValueMap = toNameValueMap(row.getFields());
                    Map<Long, Object> idKeyMap = EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(targetEntityId, nameValueMap, fieldCoreService);
                    subDataList.add(idKeyMap);
                }
            }
            SubEntityVo vo = new SubEntityVo();
            vo.setSubEntityId(targetEntityId);
            vo.setSubData(subDataList);
            list.add(vo);
        }
        return list;
    }

    /**
     * 将字段值DTO映射转换为 name->value 映射
     * @param dtoMap 字段值映射
     * @return name->value 映射
     */
    private Map<String, Object> toNameValueMap(Map<String, SemanticFieldValueDTO<Object>> dtoMap) {
        if (dtoMap == null || dtoMap.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : dtoMap.entrySet()) {
            if (e == null) {
                continue;
            }
            String key = e.getKey();
            SemanticFieldValueDTO<Object> v = e.getValue();
            Object val = v == null ? null : v.getStoreValue();
            result.put(key, val);
        }
        return result;
    }

    private String codeOf(SemanticMethodCodeEnum e) {
        return e == null ? null : e.name();
    }
}
