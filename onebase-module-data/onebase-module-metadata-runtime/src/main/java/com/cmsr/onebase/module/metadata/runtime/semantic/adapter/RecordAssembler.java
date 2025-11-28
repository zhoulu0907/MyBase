package com.cmsr.onebase.module.metadata.runtime.semantic.adapter;

import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.util.EntityFieldDataConverter;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataCreateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataGetReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataPageReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataDeleteReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataUpdateReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.ValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.MethodCodeEnum;
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
public class RecordAssembler {

    @Resource
    private MetadataEntityFieldCoreService fieldCoreService;

    /**
     * 装配创建请求
     * @param record 记录承载对象
     * @return 创建请求VO
     */
    public DynamicDataCreateReqVO toCreateReq(RecordDTO record) {
        DynamicDataCreateReqVO req = new DynamicDataCreateReqVO();
        if (record == null) {
            return req;
        }
        if (record.getContext() != null) {
            req.setTraceId(record.getContext().getTraceId());
            req.setMenuId(record.getContext().getMenuId());
            req.setMethodCode(codeOf(record.getContext().getMethodCode()));
        }
        Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
        req.setEntityId(entityId);
        Map<String, ValueDTO> dtoMap = record.getValue() != null ? record.getValue().getData() : null;
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
    public DynamicDataUpdateReqVO toUpdateReq(RecordDTO record) {
        DynamicDataUpdateReqVO req = new DynamicDataUpdateReqVO();
        if (record == null) {
            return req;
        }
        if (record.getContext() != null) {
            req.setMenuId(record.getContext().getMenuId());
            req.setMethodCode(codeOf(record.getContext().getMethodCode()));
        }
        Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getValue() != null && record.getValue().getData() != null ? record.getValue().getData().get("id") : null;
        req.setId(id);
        Map<String, ValueDTO> dtoMap = record.getValue() != null ? record.getValue().getData() : null;
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
    public DynamicDataDeleteReqVO toDeleteReq(RecordDTO record) {
        DynamicDataDeleteReqVO req = new DynamicDataDeleteReqVO();
        if (record == null) {
            return req;
        }
        if (record.getContext() != null) {
            req.setMenuId(record.getContext().getMenuId());
            req.setMethodCode(codeOf(record.getContext().getMethodCode()));
        }
        Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getValue() != null && record.getValue().getData() != null ?
                (record.getValue().getData().get("id") == null ? null : record.getValue().getData().get("id").getValue()) : null;
        req.setId(id == null ? null : Long.valueOf(String.valueOf(id)));
        return req;
    }

    /**
     * 装配详情查询请求
     * @param record 记录承载对象
     * @return 查询请求VO
     */
    public DynamicDataGetReqVO toGetReq(RecordDTO record) {
        DynamicDataGetReqVO req = new DynamicDataGetReqVO();
        if (record == null) {
            return req;
        }
        if (record.getContext() != null) {
            req.setMenuId(record.getContext().getMenuId());
            req.setMethodCode(codeOf(record.getContext().getMethodCode()));
        }
        Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
        req.setEntityId(entityId);
        Object id = record.getValue() != null && record.getValue().getData() != null ?
                (record.getValue().getData().get("id") == null ? null : record.getValue().getData().get("id").getValue()) : null;
        req.setId(id == null ? null : Long.valueOf(String.valueOf(id)));
        return req;
    }

    /**
     * 装配分页查询请求
     * @param record 记录承载对象
     * @return 分页请求VO
     */
    public DynamicDataPageReqVO toPageReq(RecordDTO record) {
        DynamicDataPageReqVO req = new DynamicDataPageReqVO();
        if (record == null) {
            return req;
        }
        if (record.getContext() != null) {
            req.setMenuId(record.getContext().getMenuId());
            req.setPageNo(record.getContext().getPageNo());
            req.setPageSize(record.getContext().getPageSize());
            if (record.getContext().getSortBy() != null && !record.getContext().getSortBy().isEmpty()) {
                var first = record.getContext().getSortBy().get(0);
                req.setSortField(first.getField());
                req.setSortDirection(first.getDirection() == null ? null : first.getDirection().name());
            }
            Map<String, Object> nameFilters = record.getContext().getFilters();
            Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
            Map<Long, Object> idFilters = nameFilters == null ? null :
                    EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(entityId, nameFilters, fieldCoreService);
            req.setFilters(idFilters);
            req.setMethodCode(codeOf(record.getContext().getMethodCode()));
        }
        Long entityId = record.getEntity() != null ? record.getEntity().getId() : null;
        req.setEntityId(entityId);
        return req;
    }

    /**
     * 构造子表数据列表：按连接器模型将行值转换为目标实体的 idKey Map
     * @param record 记录承载对象
     * @return 子表数据列表
     */
    private List<SubEntityVo> buildSubEntities(RecordDTO record) {
        List<SubEntityVo> list = new ArrayList<>();
        if (record == null || record.getValue() == null || record.getValue().getConnectors() == null) {
            return list;
        }
        Map<String, RelationValueDTO> connectors = record.getValue().getConnectors();
        List<RelationSchemaDTO> schemas = record.getEntity() != null ? record.getEntity().getConnectors() : null;
        Map<String, RelationSchemaDTO> schemaMap = new HashMap<>();
        if (schemas != null) {
            for (RelationSchemaDTO s : schemas) {
                if (s != null && s.getName() != null) {
                    schemaMap.put(s.getName(), s);
                }
            }
        }
        for (Map.Entry<String, RelationValueDTO> entry : connectors.entrySet()) {
            String name = entry.getKey();
            RelationValueDTO v = entry.getValue();
            RelationSchemaDTO s = schemaMap.get(name);
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
            if (v.getRow() != null) {
                RowValueDTO row = v.getRow();
                Map<Long, Object> idKeyMap = EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(targetEntityId, row.getFields(), fieldCoreService);
                subDataList.add(idKeyMap);
            }
            if (v.getRows() != null) {
                for (RowValueDTO row : v.getRows()) {
                    Map<Long, Object> idKeyMap = EntityFieldDataConverter.convertNameKeyMapToIdKeyMap(targetEntityId, row.getFields(), fieldCoreService);
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
    private Map<String, Object> toNameValueMap(Map<String, ValueDTO> dtoMap) {
        if (dtoMap == null || dtoMap.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, ValueDTO> e : dtoMap.entrySet()) {
            if (e == null) {
                continue;
            }
            String key = e.getKey();
            ValueDTO v = e.getValue();
            Object val = v == null ? null : v.getValue();
            result.put(key, val);
        }
        return result;
    }

    private String codeOf(MethodCodeEnum e) {
        return e == null ? null : e.name();
    }
}
