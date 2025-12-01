package com.cmsr.onebase.module.metadata.runtime.semantic.util;

/**
 * 语义引用解析器
 *
 * <p>职责：</p>
 * <ul>
 *   <li>根据字段类型对值进行“语义增强”，输出统一的 `{id,name}` 或列表结构，便于前端展示与下游处理</li>
 *   <li>支持用户、部门、数据选择（关联数据）与数据字典（系统字典/字段选项）等引用类型字段的解析</li>
 *   <li>在一次处理周期内进行批量收集与批量查询（用户/部门/字段选项），减少数据库或远程调用次数</li>
 *   <li>通过实体模型 `SemanticEntitySchemaDTO` 提供的字段与关系属性，避免运行时重复查库</li>
 * </ul>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>遵循分层架构：本类位于 runtime 层，专注值解析，不直接进行复杂业务判断</li>
 *   <li>幂等：解析仅对当前值进行转换，不影响源数据</li>
 *   <li>性能：使用统一遍历方法与批量缓存（字段选项、用户、部门），避免 N 次查询</li>
 *   <li>安全：日志避免输出敏感数据，仅输出必要调试信息</li>
 * </ul>
 */
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.RefType;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.mybatisflex.core.row.Row;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dal.database.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticTemporaryDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

@Component
@Slf4j
public class SemanticRefResolver {
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private DictDataApi dictDataApi;

    @Resource
    private DynamicMetadataRepository dynamicMetadataRepository;

    @Resource
    private SemanticTemporaryDatasourceService semanticTemporaryDatasourceService;

    /**
     * 对实体值进行语义增强
     *
     * <p>流程：</p>
     * <ol>
     *   <li>统一遍历主实体行与全部关系行，批量收集用户ID、部门ID、数据选择ID与字典字段ID</li>
     *   <li>批量查询用户、部门与字段选项，构建本次解析用的缓存</li>
     *   <li>统一再次遍历，按字段类型应用解析：USER/DEPARTMENT/DATA_SELECTION/SELECT 等</li>
     * </ol>
     *
     * @param entity 实体模型（包含字段与关系属性）
     * @param value  值模型（包含主实体与关系行的字段值）
     */
    public void enrich(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        if (value == null) return;
        Set<Long> userIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> dataSelectIds = new HashSet<>();
        Set<Long> selectFieldIds = new HashSet<>();

        forEachRow(value, fields -> collectFromFields(fields, userIds, deptIds, dataSelectIds, selectFieldIds));

        Map<Long, AdminUserRespDTO> users = userIds.isEmpty() ? Collections.emptyMap() : adminUserApi.getUserMap(userIds);
        Map<Long, DeptRespDTO> depts = deptIds.isEmpty() ? Collections.emptyMap() : deptApi.getDeptMap(deptIds);

        Map<Long, MetadataEntityFieldDO> fieldSchemaMap = buildFieldSchemaMap(entity);
        Map<Long, List<MetadataEntityFieldOptionDO>> fieldOptionsCache = selectFieldIds.isEmpty()
                ? new HashMap<>()
                : buildOptionsCacheFromEntity(entity, selectFieldIds);

        Map<Long, Map<String, String>> dictLabelCacheByFieldId = selectFieldIds.isEmpty()
                ? new HashMap<>()
                : buildDictLabelCacheByFieldId(entity, selectFieldIds, fieldSchemaMap);

        Map<Long, DataSelectMeta> dataSelectMetaBySourceFieldId = buildDataSelectMeta(entity);
        Map<String, Set<Object>> idsByTable = collectDataSelectIdsByTable(value, dataSelectMetaBySourceFieldId);
        Map<String, String> pkFieldByTable = new HashMap<>();
        for (Map.Entry<Long, DataSelectMeta> e : dataSelectMetaBySourceFieldId.entrySet()) {
            DataSelectMeta meta = e.getValue();
            if (meta != null && meta.tableName != null && meta.pkField != null) {
                pkFieldByTable.putIfAbsent(meta.tableName, meta.pkField);
            }
        }
        Map<String, Map<Object, Row>> mainsByTable = new HashMap<>();
        
        for (Map.Entry<String, Set<Object>> e : idsByTable.entrySet()) {
            String table = e.getKey();
            String pk = pkFieldByTable.get(table);
            if (pk == null) continue;
            List<Object> ids = new ArrayList<>(e.getValue());
            if (ids.isEmpty()) continue;
            List<Row> mains = dynamicMetadataRepository.selectMainByIds(table, pk, ids);
            Map<Object, Row> rowById = new HashMap<>();
            for (Row r : mains) { rowById.put(r.get(pk), r); }
            mainsByTable.put(table, rowById);
        }

        forEachRow(value, fields -> applyToFields(fields, users, depts, fieldSchemaMap, fieldOptionsCache, dictLabelCacheByFieldId, dataSelectMetaBySourceFieldId, mainsByTable));
    }

    /**
     * 统一遍历主实体与所有关系行的字段集合，并对每个字段集合执行回调
     *
     * @param value    值模型
     * @param consumer 字段集合回调
     */
    private void forEachRow(SemanticEntityValueDTO value, Consumer<Map<String, SemanticFieldValueDTO<Object>>> consumer) {
        if (value == null || consumer == null) return;
        consumer.accept(value.getFieldValueMap());
        if (value.getConnectors() != null) {
            for (SemanticRelationValueDTO rel : value.getConnectors().values()) {
                if (rel == null) continue;
                SemanticRowValueDTO one = rel.getRowValue();
                if (one != null) consumer.accept(one.getFields());
                List<SemanticRowValueDTO> list = rel.getRowValueList();
                if (list != null) {
                    for (SemanticRowValueDTO row : list) consumer.accept(row.getFields());
                }
            }
        }
    }

    private Map<Long, List<MetadataEntityFieldOptionDO>> buildOptionsCacheFromEntity(SemanticEntitySchemaDTO entity, Set<Long> selectFieldIds) {
        Map<Long, List<MetadataEntityFieldOptionDO>> map = new HashMap<>();
        if (entity != null && entity.getFields() != null) {
            for (SemanticFieldSchemaDTO f : entity.getFields()) {
                if (f == null || f.getId() == null) continue;
                if (!selectFieldIds.contains(f.getId())) continue;
                if (f.getFieldOptions() != null && !f.getFieldOptions().isEmpty()) {
                    map.put(f.getId(), f.getFieldOptions());
                }
            }
        }
        if (entity != null && entity.getConnectors() != null) {
            for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
                List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
                if (attrs == null) continue;
                for (SemanticFieldSchemaDTO f : attrs) {
                    if (f == null || f.getId() == null) continue;
                    if (!selectFieldIds.contains(f.getId())) continue;
                    if (f.getFieldOptions() != null && !f.getFieldOptions().isEmpty()) {
                        map.put(f.getId(), f.getFieldOptions());
                    }
                }
            }
        }
        log.info("fieldOptionsCache: {}", map);
        return map;
    }

    /**
     * 构建字段Schema缓存（含主实体字段与关系属性）
     *
     * @param entity 实体模型
     * @return 字段ID到字段Schema简化副本的映射
     */
    private Map<Long, MetadataEntityFieldDO> buildFieldSchemaMap(SemanticEntitySchemaDTO entity) {
        Map<Long, MetadataEntityFieldDO> map = new HashMap<>();
        if (entity != null && entity.getFields() != null) {
            for (var f : entity.getFields()) {
                if (f != null && f.getId() != null) {
                    MetadataEntityFieldDO stub = new MetadataEntityFieldDO();
                    stub.setId(f.getId());
                    stub.setFieldName(f.getFieldName());
                    stub.setFieldType(f.getFieldType());
                    stub.setDictTypeId(f.getDictTypeId());
                    map.put(f.getId(), stub);
                }
            }
        }
        if (entity != null && entity.getConnectors() != null) {
            for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
                List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
                if (attrs == null) continue;
                for (var f : attrs) {
                    if (f != null && f.getId() != null) {
                        MetadataEntityFieldDO stub = new MetadataEntityFieldDO();
                        stub.setId(f.getId());
                        stub.setFieldName(f.getFieldName());
                        stub.setFieldType(f.getFieldType());
                        stub.setDictTypeId(f.getDictTypeId());
                        map.put(f.getId(), stub);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 从字段集合中收集引用类型字段所需的批量ID或字段ID
     *
     * @param fields          字段值集合
     * @param userIds         收集的用户ID集合
     * @param deptIds         收集的部门ID集合
     * @param dataSelectIds   收集的数据选择ID集合
     * @param selectFieldIds  收集的字典字段ID集合
     */
    private void collectFromFields(Map<String, SemanticFieldValueDTO<Object>> fields, Set<Long> userIds, Set<Long> deptIds, Set<Long> dataSelectIds, Set<Long> selectFieldIds) {
        if (fields == null) return;
        for (SemanticFieldValueDTO<Object> v : fields.values()) {
            if (v == null) continue;
            SemanticFieldTypeEnum t = v.getFieldTypeEnum();
            if (t == null || !t.isRefType()) continue;
            if (t == SemanticFieldTypeEnum.USER || t == SemanticFieldTypeEnum.MULTI_USER) {
                collectIds(v, userIds);
            } else if (t == SemanticFieldTypeEnum.DEPARTMENT || t == SemanticFieldTypeEnum.MULTI_DEPARTMENT) {
                collectIds(v, deptIds);
            } else if (t == SemanticFieldTypeEnum.DATA_SELECTION || t == SemanticFieldTypeEnum.MULTI_DATA_SELECTION) {
                collectIds(v, dataSelectIds);
            } else if (t == SemanticFieldTypeEnum.SELECT || t == SemanticFieldTypeEnum.MULTI_SELECT) {
                Long fid = v.getFieldId();
                if (fid != null) selectFieldIds.add(fid);
            }
        }
    }

    /**
     * 从字段值中提取并加入ID集合
     *
     * @param v   字段值
     * @param out 目标ID集合
     */
    private void collectIds(SemanticFieldValueDTO<Object> v, Set<Long> out) {
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list != null) {
                for (Object o : list) { Long id = toLong(extractId(o)); if (id != null) out.add(id); }
            }
        } else {
            Object one = v.getValueAsBizType();
            Long id = toLong(extractId(one));
            if (id != null) out.add(id);
        }
    }

    /**
     * 提取引用类型中的ID（支持 `RefType` 与原始数值）
     *
     * @param o 原始对象
     * @return ID对象
     */
    private Object extractId(Object o) {
        if (o == null) return null;
        if (o instanceof RefType r) return r.getId();
        return o;
    }

    /**
     * 将对象安全转换为 Long
     *
     * @param v 输入对象
     * @return Long 值或 null
     */
    private Long toLong(Object v) {
        if (v == null) return null;
        try { return new BigDecimal(String.valueOf(v)).longValue(); } catch (Exception e) { return null; }
    }

    /**
     * 应用字段解析（用户、部门、数据选择、字典）
     *
     * @param fields            字段值集合
     * @param users             用户缓存
     * @param depts             部门缓存
     * @param fieldSchemaMap    字段Schema缓存
     * @param fieldOptionsCache 字段选项缓存
     */
    private void applyToFields(Map<String, SemanticFieldValueDTO<Object>> fields, Map<Long, AdminUserRespDTO> users, Map<Long, DeptRespDTO> depts, Map<Long, MetadataEntityFieldDO> fieldSchemaMap, Map<Long, List<MetadataEntityFieldOptionDO>> fieldOptionsCache, Map<Long, Map<String, String>> dictLabelCacheByFieldId, Map<Long, DataSelectMeta> dataSelectMetaBySourceFieldId, Map<String, Map<Object, Row>> mainsByTable) {
        if (fields == null) return;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
            SemanticFieldValueDTO<Object> v = e.getValue();
            if (v == null) continue;
            SemanticFieldTypeEnum t = v.getFieldTypeEnum();
            if (t == null || !t.isRefType()) continue;
            if (t == SemanticFieldTypeEnum.USER || t == SemanticFieldTypeEnum.MULTI_USER) {
                applyUser(v, users);
            } else if (t == SemanticFieldTypeEnum.DEPARTMENT || t == SemanticFieldTypeEnum.MULTI_DEPARTMENT) {
                applyDept(v, depts);
            } else if (t == SemanticFieldTypeEnum.DATA_SELECTION || t == SemanticFieldTypeEnum.MULTI_DATA_SELECTION) {
                applyDataSelectionUnified(v, dataSelectMetaBySourceFieldId, mainsByTable);
            } else if (t == SemanticFieldTypeEnum.SELECT || t == SemanticFieldTypeEnum.MULTI_SELECT) {
                applyDict(v, fieldSchemaMap, fieldOptionsCache, dictLabelCacheByFieldId);
            }
        }
    }

    /**
     * 解析用户字段：输出 `{id, name}` 或列表
     */
    private void applyUser(SemanticFieldValueDTO<Object> v, Map<Long, AdminUserRespDTO> users) {
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String,Object>> mapped = new ArrayList<>();
            for (Object o : list) {
                Long id = toLong(extractId(o));
                Map<String,Object> m = new HashMap<>();
                m.put("id", id);
                AdminUserRespDTO u = id == null ? null : users.get(id);
                if (u != null) m.put("name", u.getNickname());
                mapped.add(m);
            }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            Long id = toLong(extractId(one));
            Map<String,Object> m = new HashMap<>();
            m.put("id", id);
            AdminUserRespDTO u = id == null ? null : users.get(id);
            if (u != null) m.put("name", u.getNickname());
            v.setRawValue(m);
        }
    }

    /**
     * 解析部门字段：输出 `{id, name}` 或列表
     */
    private void applyDept(SemanticFieldValueDTO<Object> v, Map<Long, DeptRespDTO> depts) {
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String,Object>> mapped = new ArrayList<>();
            for (Object o : list) {
                Long id = toLong(extractId(o));
                Map<String,Object> m = new HashMap<>();
                m.put("id", id);
                DeptRespDTO d = id == null ? null : depts.get(id);
                if (d != null) m.put("name", d.getName());
                mapped.add(m);
            }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            Long id = toLong(extractId(one));
            Map<String,Object> m = new HashMap<>();
            m.put("id", id);
            DeptRespDTO d = id == null ? null : depts.get(id);
            if (d != null) m.put("name", d.getName());
            v.setRawValue(m);
        }
    }

    /**
     * 解析数据选择字段：从引用值里提取 `id/name`
     */
    private void applyDataSelectionUnified(SemanticFieldValueDTO<Object> v, Map<Long, DataSelectMeta> metaByFieldId, Map<String, Map<Object, Row>> mainsByTable) {
        Long fieldId = v.getFieldId();
        DataSelectMeta meta = fieldId == null ? null : metaByFieldId.get(fieldId);
        if (meta == null) { applyDataSelectionFallback(v); return; }
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String, Object>> mapped = new ArrayList<>();
            Map<Object, Row> byId = mainsByTable.get(meta.tableName);
            for (Object o : list) {
                Object idObj = extractId(o);
                Row r = byId == null ? null : byId.get(idObj);
                Map<String, Object> m = new HashMap<>();
                m.put("id", idObj);
                if (r != null && meta.selectFieldName != null) { m.put("name", r.get(meta.selectFieldName)); }
                mapped.add(m);
            }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            Object idObj = extractId(one);
            Map<Object, Row> byId = mainsByTable.get(meta.tableName);
            Row r = byId == null ? null : byId.get(idObj);
            Map<String, Object> m = new HashMap<>();
            m.put("id", idObj);
            if (r != null && meta.selectFieldName != null) { m.put("name", r.get(meta.selectFieldName)); }
            v.setRawValue(m);
        }
    }

    private void applyDataSelectionFallback(SemanticFieldValueDTO<Object> v) {
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String, Object>> mapped = new ArrayList<>();
            for (Object o : list) {
                Long id = toLong(extractId(o));
                Map<String, Object> m = new HashMap<>();
                m.put("id", id);
                String name = extractName(o);
                if (name != null) m.put("name", name);
                mapped.add(m);
            }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            Long id = toLong(extractId(one));
            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            String name = extractName(one);
            if (name != null) m.put("name", name);
            v.setRawValue(m);
        }
    }

    private Map<Long, DataSelectMeta> buildDataSelectMeta(SemanticEntitySchemaDTO entity) {
        Map<Long, DataSelectMeta> map = new HashMap<>();
        if (entity == null || entity.getConnectors() == null) return map;
        for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
            if (c == null || c.getRelationshipType() == null) continue;
            if (!RelationshipTypeEnum.isDataSelectRelationship(c.getRelationshipType().getRelationshipType())) continue;
            String pkField = getPrimaryKeyNameFromConnector(c);
            Long selectFieldId = c.getSelectFieldId() == null ? null : Long.valueOf(c.getSelectFieldId());
            String selectFieldName = getFieldNameByIdFromConnector(c, selectFieldId);
            Long sourceFieldId = c.getSourceKeyFieldId();
            if (sourceFieldId == null || pkField == null) continue;
            DataSelectMeta meta = new DataSelectMeta();
            meta.tableName = c.getTargetEntityTableName();
            meta.pkField = pkField;
            meta.selectFieldName = selectFieldName;
            map.put(sourceFieldId, meta);
        }
        return map;
    }

    private Map<String, Set<Object>> collectDataSelectIdsByTable(SemanticEntityValueDTO value, Map<Long, DataSelectMeta> metaByFieldId) {
        Map<String, Set<Object>> idsByTable = new HashMap<>();
        forEachRow(value, fields -> {
            if (fields == null) return;
            for (SemanticFieldValueDTO<Object> v : fields.values()) {
                if (v == null) continue;
                SemanticFieldTypeEnum t = v.getFieldTypeEnum();
                if (t != SemanticFieldTypeEnum.DATA_SELECTION && t != SemanticFieldTypeEnum.MULTI_DATA_SELECTION) continue;
                Long fieldId = v.getFieldId();
                DataSelectMeta meta = fieldId == null ? null : metaByFieldId.get(fieldId);
                if (meta == null || meta.tableName == null) continue;
                if (v.isListType()) {
                    List<?> list = v.getValueAsBizList();
                    if (list != null) {
                        for (Object o : list) {
                            Object idObj = extractId(o);
                            idsByTable.computeIfAbsent(meta.tableName, k -> new HashSet<>()).add(idObj);
                        }
                    }
                } else {
                    Object one = v.getValueAsBizType();
                    Object idObj = extractId(one);
                    idsByTable.computeIfAbsent(meta.tableName, k -> new HashSet<>()).add(idObj);
                }
            }
        });
        return idsByTable;
    }

    private static class DataSelectMeta {
        String tableName;
        String pkField;
        String selectFieldName;
    }

    /**
     * 解析字典字段：
     * <ul>
     *   <li>绑定系统字典（`dictTypeId != null`）：直接使用值的字符串表示作为显示名</li>
     *   <li>未绑定系统字典：从字段选项缓存中匹配 `option_value -> option_label`</li>
     * </ul>
     */
    private void applyDict(SemanticFieldValueDTO<Object> v, Map<Long, MetadataEntityFieldDO> fieldSchemaMap, Map<Long, List<MetadataEntityFieldOptionDO>> fieldOptionsCache, Map<Long, Map<String, String>> dictLabelCacheByFieldId) {
        Long fieldId = v.getFieldId();
        MetadataEntityFieldDO field = fieldId == null ? null : fieldSchemaMap.get(fieldId);
        Long dictTypeId = field == null ? null : field.getDictTypeId();

        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String, Object>> mapped = new ArrayList<>();
            for (Object o : list) {
                Object idOrVal = extractIdOrValue(o);
                Map<String, Object> m = new HashMap<>();
                m.put("id", idOrVal);
                String label = resolveDictLabelOrOption(dictTypeId, fieldId, idOrVal, fieldOptionsCache, dictLabelCacheByFieldId);
                if (label != null) m.put("name", label);
                mapped.add(m);
            }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            Object idOrVal = extractIdOrValue(one);
            Map<String, Object> m = new HashMap<>();
            m.put("id", idOrVal);
            String label = resolveDictLabelOrOption(dictTypeId, fieldId, idOrVal, fieldOptionsCache, dictLabelCacheByFieldId);
            if (label != null) m.put("name", label);
            v.setRawValue(m);
        }
    }

    private void applyDataSelectionAssignments(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        if (entity == null || value == null || entity.getConnectors() == null) return;
        Object parentId = value.getId();
        if (parentId == null) return;
        Map<String, Set<Object>> idsByTable = new HashMap<>();
        Map<String, String> pkFieldByTable = new HashMap<>();
        List<ConnectorAssign> assigns = new ArrayList<>();

        for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
            if (c == null || c.getRelationshipType() == null) continue;
            if (!RelationshipTypeEnum.isDataSelectRelationship(c.getRelationshipType().getRelationshipType())) continue;
            String relationKey = getFieldNameByIdFromConnector(c, c.getTargetKeyFieldId());
            Long selectFieldId = c.getSelectFieldId() == null ? null : Long.valueOf(c.getSelectFieldId());
            String selectFieldName = getFieldNameByIdFromConnector(c, selectFieldId);
            String pkField = getPrimaryKeyNameFromConnector(c);
            if (relationKey == null || pkField == null) continue;
            List<Row> rows = dynamicMetadataRepository.selectRelationRowsByParent(c.getTargetEntityTableName(), relationKey, parentId);
            if (rows == null || rows.isEmpty()) continue;
            List<Object> ids = new ArrayList<>();
            for (var r : rows) { ids.add(r.get(pkField)); }
            idsByTable.computeIfAbsent(c.getTargetEntityTableName(), k -> new HashSet<>()).addAll(ids);
            pkFieldByTable.putIfAbsent(c.getTargetEntityTableName(), pkField);

            Long sourceFieldId = c.getSourceKeyFieldId();
            String sourceFieldName = resolveSourceFieldName(entity, sourceFieldId);
            if (sourceFieldName == null) continue;

            ConnectorAssign a = new ConnectorAssign();
            a.tableName = c.getTargetEntityTableName();
            a.pkField = pkField;
            a.selectFieldName = selectFieldName;
            a.sourceFieldId = sourceFieldId;
            a.sourceFieldName = sourceFieldName;
            a.single = c.getRelationshipType() == RelationshipTypeEnum.DATA_SELECT;
            a.ids = ids;
            assigns.add(a);
        }

        Map<String, Map<Object, Row>> mainsByTable = new HashMap<>();
        for (Map.Entry<String, Set<Object>> e : idsByTable.entrySet()) {
            String table = e.getKey();
            String pk = pkFieldByTable.get(table);
            if (pk == null) continue;
            List<Object> ids = new ArrayList<>(e.getValue());
            if (ids.isEmpty()) continue;
            List<Row> mains = dynamicMetadataRepository.selectMainByIds(table, pk, ids);
            Map<Object, Row> byId = new HashMap<>();
            for (var r : mains) { byId.put(r.get(pk), r); }
            mainsByTable.put(table, byId);
        }

        for (ConnectorAssign a : assigns) {
            Map<Object, Row> byId = mainsByTable.get(a.tableName);
            if (byId == null) continue;
            List<Map<String, Object>> list = new ArrayList<>();
            for (Object idObj : a.ids) {
                Row r = byId.get(idObj);
                if (r == null) continue;
                Map<String, Object> m = new HashMap<>();
                m.put("id", r.get(a.pkField));
                if (a.selectFieldName != null) { m.put("name", r.get(a.selectFieldName)); }
                list.add(m);
            }
            SemanticFieldValueDTO<Object> v;
            if (a.single) {
                Map<String, Object> one = list.isEmpty() ? null : list.get(0);
                v = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.DATA_SELECTION);
                v.setRawValue(one);
            } else {
                v = SemanticFieldValueDTO.ofType(SemanticFieldTypeEnum.MULTI_DATA_SELECTION);
                v.setRawValue(list);
            }
            v.setFieldId(a.sourceFieldId);
            v.setFieldName(a.sourceFieldName);
            if (value.getFieldValueMap() == null) { value.setFieldValueMap(new HashMap<>()); }
            value.getFieldValueMap().put(a.sourceFieldName, v);
        }
    }

    private static class ConnectorAssign {
        String tableName;
        String pkField;
        String selectFieldName;
        Long sourceFieldId;
        String sourceFieldName;
        boolean single;
        List<Object> ids;
    }

    private String resolveSourceFieldName(SemanticEntitySchemaDTO entity, Long fieldId) {
        if (entity == null || entity.getFields() == null || fieldId == null) return null;
        for (SemanticFieldSchemaDTO f : entity.getFields()) {
            if (f != null && fieldId.equals(f.getId())) return f.getFieldName();
        }
        return null;
    }

    private String getPrimaryKeyNameFromConnector(SemanticRelationSchemaDTO c) {
        if (c == null || c.getRelationAttributes() == null) return null;
        for (SemanticFieldSchemaDTO f : c.getRelationAttributes()) {
            if (f != null && Boolean.TRUE.equals(f.getIsPrimaryKey())) return f.getFieldName();
        }
        return "id";
    }

    private String getFieldNameByIdFromConnector(SemanticRelationSchemaDTO c, Long fieldId) {
        if (c == null || c.getRelationAttributes() == null || fieldId == null) return null;
        for (SemanticFieldSchemaDTO f : c.getRelationAttributes()) {
            if (f != null && fieldId.equals(f.getId())) return f.getFieldName();
        }
        return null;
    }

    /**
     * 提取引用名：支持 `RefType` 与 `{name: ...}` Map
     */
    private String extractName(Object o) {
        if (o == null) return null;
        if (o instanceof RefType r) return r.getName();
        if (o instanceof Map<?,?> m) {
            Object n = m.get("name");
            return n == null ? null : String.valueOf(n);
        }
        return null;
    }

    /**
     * 提取字典解析的候选值：优先 `id`，其次 `value`，否则原值
     */
    private Object extractIdOrValue(Object o) {
        if (o == null) return null;
        if (o instanceof RefType r) return r.getId();
        if (o instanceof Map<?,?> m) {
            Object id = m.get("id");
            if (id != null) return id;
            Object val = m.get("value");
            return val;
        }
        return o;
    }

    /**
     * 映射字典显示名：
     * <ul>
     *   <li>系统字典：返回字符串化的值</li>
     *   <li>字段选项：从缓存中匹配并返回 `option_label`</li>
     * </ul>
     */
    private String resolveDictLabelOrOption(Long dictTypeId, Long fieldId, Object idOrVal, Map<Long, List<MetadataEntityFieldOptionDO>> fieldOptionsCache, Map<Long, Map<String, String>> dictLabelCacheByFieldId) {
        if (idOrVal == null) return null;
        if (dictTypeId != null) {
            Map<String, String> labelMap = fieldId == null ? null : dictLabelCacheByFieldId.get(fieldId);
            if (labelMap == null || labelMap.isEmpty()) return null;
            String key = String.valueOf(idOrVal);
            String byKey = labelMap.get(key);
            if (byKey != null) return byKey;
            return null;
        }
        if (fieldId != null) {
            List<MetadataEntityFieldOptionDO> options = fieldOptionsCache.get(fieldId);
            if (options == null || options.isEmpty()) return null;
            String valueStr = String.valueOf(idOrVal);
            for (MetadataEntityFieldOptionDO opt : options) {
                if (opt.getOptionValue() != null && opt.getOptionValue().equals(valueStr)) {
                    return opt.getOptionLabel();
                }
            }
        }
        return null;
    }

    private Map<Long, Map<String, String>> buildDictLabelCacheByFieldId(SemanticEntitySchemaDTO entity, Set<Long> selectFieldIds, Map<Long, MetadataEntityFieldDO> fieldSchemaMap) {
        Map<Long, Map<String, String>> result = new HashMap<>();
        if (selectFieldIds == null || selectFieldIds.isEmpty()) return result;

        Map<Long, Long> fieldIdToDictTypeId = new HashMap<>();
        Set<Long> dictTypeIds = new HashSet<>();
        for (Long fieldId : selectFieldIds) {
            MetadataEntityFieldDO field = fieldSchemaMap.get(fieldId);
            if (field == null || field.getDictTypeId() == null) continue;
            fieldIdToDictTypeId.put(fieldId, field.getDictTypeId());
            dictTypeIds.add(field.getDictTypeId());
        }
        if (dictTypeIds.isEmpty()) return result;

        Map<Long, List<DictDataRespDTO>> dictDataBatch = dictDataApi.getDictDataListByTypeIds(dictTypeIds).getCheckedData();
        
        log.info("dictDataBatch: {}", dictDataBatch);
        if (dictDataBatch == null || dictDataBatch.isEmpty()) return result;

        for (Map.Entry<Long, Long> entry : fieldIdToDictTypeId.entrySet()) {
            Long fieldId = entry.getKey();
            Long dictTypeId = entry.getValue();
            List<DictDataRespDTO> list = dictDataBatch.get(dictTypeId);
            if (list == null || list.isEmpty()) continue;
            Map<String, String> labelMap = new HashMap<>();
            for (DictDataRespDTO d : list) {
                if (d.getId() != null) { labelMap.put(String.valueOf(d.getId()), d.getLabel()); }
                if (d.getValue() != null) { labelMap.put(String.valueOf(d.getValue()), d.getLabel()); }
            }
            result.put(fieldId, labelMap);
        }
        return result;
    }
}
