package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.type.RefType;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldOptionDTO;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.mybatisflex.core.row.Row;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileListRespDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dal.DynamicMetadataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private FileApi fileApi;

    @Resource
    private DynamicMetadataRepository dynamicMetadataRepository;

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
        Set<Long> userIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> dataSelectIds = new HashSet<>();
        Set<Long> selectFieldIds = new HashSet<>();
        Set<Long> fileIds = new HashSet<>();

        forEachRow(value, fields -> collectFromFields(fields, userIds, deptIds, dataSelectIds, selectFieldIds, fileIds));

        // 构建用户缓存（按收集的用户ID批量查询，供 USER/MULTI_USER 解析）
        Map<Long, AdminUserRespDTO> users = buildUserCache(userIds);
        // 构建部门缓存（按收集的部门ID批量查询，供 DEPARTMENT/MULTI_DEPARTMENT 解析）
        Map<Long, DeptRespDTO> depts = buildDeptCache(deptIds);
        // 构建字段Schema缓存（主实体字段与关系属性），用于字典解析等场景
        Map<Long, MetadataEntityFieldDO> fieldSchemaMap = buildFieldSchemaMap(entity);
        // 构建字段选项缓存（仅针对 SELECT/MULTI_SELECT 且未绑定系统字典的字段）
        Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache = buildOptionsCacheFromEntity(entity, selectFieldIds);
        // 构建字典标签缓存（按字段ID聚合系统字典的 id/value -> label 映射）
        Map<Long, Map<String, String>> dictLabelCacheByFieldId = buildDictLabelCacheByFieldId(entity, selectFieldIds, fieldSchemaMap);
        // 构建数据选择上下文（关系元信息 + 主数据缓存），供 DATA_SELECTION/MULTI_DATA_SELECTION 解析
        DataSelectionContext dataSelection = buildDataSelectionContext(entity, value);

        log.info("dataSelectIds: {} \n dataSelection: {}", dataSelectIds, dataSelection);

        // 构建文件缓存（按收集的文件ID批量查询，供 FILE/IMAGE 解析）
        Map<Long, FileListRespDTO> files = buildFileCache(fileIds);

        ResolveContext context = new ResolveContext(users, depts, fieldSchemaMap, fieldOptionsCache, dictLabelCacheByFieldId, dataSelection.metaByFieldUuid, dataSelection.mainsByTable, files);

        if (log.isDebugEnabled()) { log.debug("mainsByTable: {}", dataSelection.mainsByTable); }

        forEachRow(value, fields -> applyToFields(fields, context));
    }

    public void enrichBatch(SemanticEntitySchemaDTO entity, List<SemanticEntityValueDTO> values) {
        if (values == null || values.isEmpty()) { return; }
        Set<Long> userIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> dataSelectIds = new HashSet<>();
        Set<Long> selectFieldIds = new HashSet<>();
        Set<Long> fileIds = new HashSet<>();

        for (SemanticEntityValueDTO v : values) {
            forEachRow(v, fields -> collectFromFields(fields, userIds, deptIds, dataSelectIds, selectFieldIds, fileIds));
        }

        Map<Long, AdminUserRespDTO> users = buildUserCache(userIds);
        Map<Long, DeptRespDTO> depts = buildDeptCache(deptIds);
        Map<Long, MetadataEntityFieldDO> fieldSchemaMap = buildFieldSchemaMap(entity);
        Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache = buildOptionsCacheFromEntity(entity, selectFieldIds);
        Map<Long, Map<String, String>> dictLabelCacheByFieldId = buildDictLabelCacheByFieldId(entity, selectFieldIds, fieldSchemaMap);
        DataSelectionContext dataSelection = buildDataSelectionContext(entity, values.get(0));
        Map<Long, FileListRespDTO> files = buildFileCache(fileIds);

        ResolveContext context = new ResolveContext(users, depts, fieldSchemaMap, fieldOptionsCache, dictLabelCacheByFieldId, dataSelection.metaByFieldUuid, dataSelection.mainsByTable, files);
        for (SemanticEntityValueDTO v : values) { forEachRow(v, fields -> applyToFields(fields, context)); }
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

    /**
     * 构建字段选项缓存
     *
     * <p>从实体与关系属性中提取已定义的字段选项，以字段ID为键缓存，供字典解析使用。</p>
     *
     * @param entity         实体Schema
     * @param selectFieldIds 需要解析的字典字段ID集合
     * @return 字段ID到其选项列表的映射
     */
    private Map<Long, List<SemanticFieldOptionDTO>> buildOptionsCacheFromEntity(SemanticEntitySchemaDTO entity, Set<Long> selectFieldIds) {
        Map<Long, List<SemanticFieldOptionDTO>> map = new HashMap<>();
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
        if (log.isDebugEnabled()) { log.debug("fieldOptionsCache: {}", map); }
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
                    map.put(f.getId(), toFieldStub(f));
                }
            }
        }
        if (entity != null && entity.getConnectors() != null) {
            for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
                List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
                if (attrs == null) continue;
                for (var f : attrs) {
                    if (f != null && f.getId() != null) {
                        map.put(f.getId(), toFieldStub(f));
                    }
                }
            }
        }
        return map;
    }

    /**
     * 将运行时字段Schema转换为轻量副本，便于解析阶段快速访问
     */
    private MetadataEntityFieldDO toFieldStub(SemanticFieldSchemaDTO f) {
        MetadataEntityFieldDO stub = new MetadataEntityFieldDO();
        stub.setId(f.getId());
        stub.setFieldName(f.getFieldName());
        stub.setFieldType(f.getFieldType());
        stub.setDictTypeId(f.getDictTypeId());
        return stub;
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
    private void collectFromFields(Map<String, SemanticFieldValueDTO<Object>> fields, Set<Long> userIds, Set<Long> deptIds, Set<Long> dataSelectIds, Set<Long> selectFieldIds, Set<Long> fileIds) {
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
            } else if (t == SemanticFieldTypeEnum.FILE || t == SemanticFieldTypeEnum.IMAGE) {
                collectIds(v, fileIds);
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
     * 统一处理列表或单值映射
     *
     * <p>根据字段是否为列表，分别对每个元素或单个值应用映射器，最终写入 rawValue。</p>
     *
     * @param v      字段值
     * @param mapper 单元素到标准输出 Map 的映射函数
     */
    private void mapListOrSingle(SemanticFieldValueDTO<Object> v, Function<Object, Map<String, Object>> mapper) {
        if (v.isListType()) {
            List<?> list = v.getValueAsBizList();
            if (list == null) return;
            List<Map<String, Object>> mapped = new ArrayList<>();
            for (Object o : list) { mapped.add(mapper.apply(o)); }
            v.setRawValue(mapped);
        } else {
            Object one = v.getValueAsBizType();
            v.setRawValue(mapper.apply(one));
        }
    }

    /**
     * 应用字段解析（用户、部门、数据选择、字典）
     *
     * <p>根据字段类型对当前字段值进行语义增强，使用上下文内的批量缓存避免重复查询。</p>
     *
     * @param fields 字段值集合
     * @param ctx    解析上下文（含用户/部门/字典/数据选择等缓存）
     */
    private void applyToFields(Map<String, SemanticFieldValueDTO<Object>> fields, ResolveContext ctx) {
        if (fields == null || ctx == null) return;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
            SemanticFieldValueDTO<Object> v = e.getValue();
            if (v == null) continue;
            SemanticFieldTypeEnum t = v.getFieldTypeEnum();
            if (t == null || !t.isRefType()) continue;
            if (t == SemanticFieldTypeEnum.USER || t == SemanticFieldTypeEnum.MULTI_USER) {
                applyUser(v, ctx.users);
            } else if (t == SemanticFieldTypeEnum.DEPARTMENT || t == SemanticFieldTypeEnum.MULTI_DEPARTMENT) {
                applyDept(v, ctx.depts);
            } else if (t == SemanticFieldTypeEnum.DATA_SELECTION || t == SemanticFieldTypeEnum.MULTI_DATA_SELECTION) {
                applyDataSelectionUnified(v, ctx.dataSelectMetaBySourceFieldUuid, ctx.mainsByTable);
            } else if (t == SemanticFieldTypeEnum.SELECT || t == SemanticFieldTypeEnum.MULTI_SELECT) {
                applyDict(v, ctx.fieldSchemaMap, ctx.fieldOptionsCache, ctx.dictLabelCacheByFieldId);
            } else if (t == SemanticFieldTypeEnum.FILE || t == SemanticFieldTypeEnum.IMAGE) {
                mapListOrSingle(v, o -> {
                    Long id = toLong(extractId(o));
                    return mapFileInfo(id, id == null ? null : ctx.files.get(id));
                });
            }
        }
    }


    /**
     * 构建文件信息缓存
     *
     * @param fileIds 需查询的文件ID集合
     * @return 文件ID到文件详情的映射
     */
    private Map<Long, FileListRespDTO> buildFileCache(Set<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) return Collections.emptyMap();
        List<FileListRespDTO> list = fileApi.getFileListByIds(fileIds).getCheckedData();
        if (list == null || list.isEmpty()) return Collections.emptyMap();
        Map<Long, FileListRespDTO> map = new HashMap<>();
        for (FileListRespDTO f : list) {
            if (f.getId() != null) map.put(f.getId(), f);
        }
        return map;
    }

    /**
     * 构建用户缓存
     *
     * <p>按已收集的用户ID批量查询，供 USER/MULTI_USER 字段解析使用。</p>
     *
     * @param userIds 用户ID集合
     * @return 用户ID到用户信息的映射
     */
    private Map<Long, AdminUserRespDTO> buildUserCache(Set<Long> userIds) {
        return userIds == null || userIds.isEmpty() ? Collections.emptyMap() : adminUserApi.getUserMap(userIds);
    }

    /**
     * 构建部门缓存
     *
     * <p>按已收集的部门ID批量查询，供 DEPARTMENT/MULTI_DEPARTMENT 字段解析使用。</p>
     *
     * @param deptIds 部门ID集合
     * @return 部门ID到部门信息的映射
     */
    private Map<Long, DeptRespDTO> buildDeptCache(Set<Long> deptIds) {
        return deptIds == null || deptIds.isEmpty() ? Collections.emptyMap() : deptApi.getDeptMap(deptIds);
    }

    /**
     * 从数据选择元信息构建表主键字段名映射
     *
     * @param metaByFieldUuid 源字段UUID到数据选择元信息映射
     * @return 表名到主键字段名的映射
     */
    private Map<String, String> buildPkFieldByTableFromMeta(Map<String, DataSelectMeta> metaByFieldUuid) {
        Map<String, String> pkFieldByTable = new HashMap<>();
        if (metaByFieldUuid == null || metaByFieldUuid.isEmpty()) return pkFieldByTable;
        for (Map.Entry<String, DataSelectMeta> e : metaByFieldUuid.entrySet()) {
            DataSelectMeta meta = e.getValue();
            if (meta != null && meta.tableName != null && meta.pkField != null) {
                pkFieldByTable.putIfAbsent(meta.tableName, meta.pkField);
            }
        }
        return pkFieldByTable;
    }

    /**
     * 构建各目标表的主数据缓存
     *
     * <p>根据按表收集的引用ID与主键字段名，批量查询并构建 `{id -> Row}` 映射。</p>
     *
     * @param idsByTable     表名到引用ID集合的映射
     * @param pkFieldByTable 表名到主键字段名的映射
     * @return 表名到 `(主键值 -> Row)` 的映射
     */
    private Map<String, Map<Object, Row>> buildMainsByTable(Map<String, Set<Object>> idsByTable, Map<String, String> pkFieldByTable) {
        Map<String, Map<Object, Row>> mainsByTable = new HashMap<>();
        if (idsByTable == null || idsByTable.isEmpty()) return mainsByTable;
        if (log.isDebugEnabled()) { log.debug("idsByTable: {}", idsByTable); }
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
        return mainsByTable;
    }

    /**
     * 构建数据选择上下文
     *
     * <p>封装数据选择的关系元信息与主数据缓存，供 DATA_SELECTION/MULTI_DATA_SELECTION 解析。</p>
     *
     * @param entity 实体Schema
     * @param value  值模型
     * @return 数据选择上下文
     */
    private DataSelectionContext buildDataSelectionContext(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        Map<String, DataSelectMeta> metaByFieldUuid = buildDataSelectMeta(entity);
        Map<String, Set<Object>> idsByTable = collectDataSelectIdsByTable(value, metaByFieldUuid);
        Map<String, String> pkFieldByTable = buildPkFieldByTableFromMeta(metaByFieldUuid);
        Map<String, Map<Object, Row>> mainsByTable = buildMainsByTable(idsByTable, pkFieldByTable);
        log.info("mainsByTable: {} , pkFieldByTable: {}, metaByFieldUuid: {}, idsByTable: {}", mainsByTable , pkFieldByTable, metaByFieldUuid, idsByTable);
        return new DataSelectionContext(metaByFieldUuid, mainsByTable);
    }

    

    /**
     * 构建文件字段的标准输出结构
     */
    private Map<String, Object> mapFileInfo(Long id, FileListRespDTO info) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        if (info != null) {
            m.put("name", info.getName());
            m.put("path", info.getPath());
            m.put("type", info.getType());
            m.put("size", info.getSize());
        }
        return m;
    }

    /**
     * 解析用户字段：输出 `{id, name}` 或列表
     */
    private void applyUser(SemanticFieldValueDTO<Object> v, Map<Long, AdminUserRespDTO> users) {
        applyEntityRef(v, users, AdminUserRespDTO::getNickname);
    }

    /**
     * 解析部门字段：输出 `{id, name}` 或列表
     */
    private void applyDept(SemanticFieldValueDTO<Object> v, Map<Long, DeptRespDTO> depts) {
        applyEntityRef(v, depts, DeptRespDTO::getName);
    }

    /**
     * 通用引用类型解析：将 ID 映射为 `{id, name}`
     */
    private <T> void applyEntityRef(SemanticFieldValueDTO<Object> v, Map<Long, T> cache, Function<T, String> nameExtractor) {
        mapListOrSingle(v, o -> {
            Long id = toLong(extractId(o));
            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            T dto = id == null ? null : cache.get(id);
            if (dto != null) m.put("name", nameExtractor.apply(dto));
            return m;
        });
    }

    /**
     * 解析数据选择字段：从引用值里提取 `id/name`
     */
    private void applyDataSelectionUnified(SemanticFieldValueDTO<Object> v, Map<String, DataSelectMeta> metaByFieldUuid, Map<String, Map<Object, Row>> mainsByTable) {
        String fieldUuid = v.getFieldUuid();
        DataSelectMeta meta = fieldUuid == null ? null : metaByFieldUuid.get(fieldUuid);
        if (meta == null) { applyDataSelectionFallback(v); return; }
        Map<Object, Row> byId = mainsByTable.get(meta.tableName);
        mapListOrSingle(v, o -> {
            Object idObj = extractId(o);
            Row r = byId == null ? null : byId.get(idObj);
            Map<String, Object> m = new HashMap<>();
            m.put("id", idObj);
            if (r != null && meta.selectFieldName != null) { m.put("name", r.get(meta.selectFieldName)); }
            return m;
        });
    }

    /**
     * 数据选择字段的回退解析
     *
     * <p>在缺少关系元信息或主数据缓存时，尝试从值中直接提取 `id/name`。</p>
     */
    private void applyDataSelectionFallback(SemanticFieldValueDTO<Object> v) {
        mapListOrSingle(v, o -> {
            Long id = toLong(extractId(o));
            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            String name = extractName(o);
            if (name != null) m.put("name", name);
            return m;
        });
    }

    /**
     * 构建数据选择关系的元信息缓存
     *
     * @param entity 实体Schema
     * @return 源字段ID到目标表/主键/显示字段等元信息的映射
     */
    private Map<String, DataSelectMeta> buildDataSelectMeta(SemanticEntitySchemaDTO entity) {
        Map<String, DataSelectMeta> map = new HashMap<>();
        if (entity == null || entity.getConnectors() == null) return map;
        for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
            if (log.isDebugEnabled()) { log.debug("connector: {}", c); }
            if (c == null || c.getRelationshipType() == null) continue;
            if (!RelationshipTypeEnum.isDataSelectRelationship(c.getRelationshipType().getRelationshipType())) continue;
            String pkField = getPrimaryKeyNameFromConnector(c);
            String selectFieldUuid = c.getSelectFieldUuid() == null ? null : c.getSelectFieldUuid();
            String selectFieldName = getFieldNameByUuidFromConnector(c, selectFieldUuid);
            String sourceKeyFieldUuid = c.getSourceKeyFieldUuid() == null ? null : c.getSourceKeyFieldUuid();
            if (selectFieldUuid == null || pkField == null) continue;
            DataSelectMeta meta = new DataSelectMeta();
            meta.tableName = c.getTargetEntityTableName();
            meta.pkField = pkField;
            meta.selectFieldName = selectFieldName;
            map.put(sourceKeyFieldUuid, meta);
            log.info("tableName is {}, pkField is {}, selectFieldName is {}", meta.tableName, meta.pkField, meta.selectFieldName);
        }
        return map;
    }

    /**
     * 按表收集数据选择字段的引用ID集合
     *
     * @param value        值模型
     * @param metaByFieldId 源字段ID到数据选择元信息映射
     * @return 表名到引用ID集合的映射
     */
    private Map<String, Set<Object>> collectDataSelectIdsByTable(SemanticEntityValueDTO value, Map<String, DataSelectMeta> metaByFieldId) {
        Map<String, Set<Object>> idsByTable = new HashMap<>();
        forEachRow(value, fields -> {
            if (fields == null) return;
            for (SemanticFieldValueDTO<Object> v : fields.values()) {
                if (v == null) continue;
                SemanticFieldTypeEnum t = v.getFieldTypeEnum();
                if (t != SemanticFieldTypeEnum.DATA_SELECTION && t != SemanticFieldTypeEnum.MULTI_DATA_SELECTION) continue;
                String fieldUuid = v.getFieldUuid();
                log.info("fieldUuid is {}", fieldUuid);
                DataSelectMeta meta = fieldUuid == null ? null : metaByFieldId.get(fieldUuid);
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

        @Override
        public String toString() {
            return JsonUtils.toJsonPrettyString(this);
        }
    }

    /**
     * 数据选择解析上下文
     *
     * <p>包含：源字段ID到选择关系元信息的映射，以及目标表主数据缓存。</p>
     */
    private static class DataSelectionContext {
        final Map<String, DataSelectMeta> metaByFieldUuid;
        final Map<String, Map<Object, Row>> mainsByTable;

        DataSelectionContext(Map<String, DataSelectMeta> metaByFieldUuid, Map<String, Map<Object, Row>> mainsByTable) {
            this.metaByFieldUuid = metaByFieldUuid;
            this.mainsByTable = mainsByTable;
        }

        @Override
        public String toString() {
            return JsonUtils.toJsonPrettyString(this);
        }
    }

    /**
     * 解析字典字段：
     * <ul>
     *   <li>绑定系统字典（`dictTypeId != null`）：直接使用值的字符串表示作为显示名</li>
     *   <li>未绑定系统字典：从字段选项缓存中匹配 `option_value -> option_label`</li>
     * </ul>
     */
    private void applyDict(SemanticFieldValueDTO<Object> v, Map<Long, MetadataEntityFieldDO> fieldSchemaMap, Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache, Map<Long, Map<String, String>> dictLabelCacheByFieldId) {
        Long fieldId = v.getFieldId();
        MetadataEntityFieldDO field = fieldId == null ? null : fieldSchemaMap.get(fieldId);
        Long dictTypeId = field == null ? null : field.getDictTypeId();

        mapListOrSingle(v, o -> {
            Object idOrVal = extractIdOrValue(o);
            Map<String, Object> m = new HashMap<>();
            m.put("id", idOrVal);
            String label = resolveDictLabelOrOption(dictTypeId, fieldId, idOrVal, fieldOptionsCache, dictLabelCacheByFieldId);
            if (label != null) m.put("name", label);
            return m;
        });
    }

    

    /**
     * 从关系属性中解析主键字段名
     *
     * @param c 关系Schema
     * @return 主键字段名，默认 `id`
     */
    private String getPrimaryKeyNameFromConnector(SemanticRelationSchemaDTO c) {
        if (c == null || c.getRelationAttributes() == null) return null;
        for (SemanticFieldSchemaDTO f : c.getRelationAttributes()) {
            if (f != null && Boolean.TRUE.equals(f.getIsPrimaryKey())) return f.getFieldName();
        }
        return "id";
    }

    /**
     * 在关系属性中根据字段ID获取字段名
     *
     * @param c       关系Schema
     * @param fieldUuid 字段UUID
     * @return 字段名或 null
     */
    private String getFieldNameByUuidFromConnector(SemanticRelationSchemaDTO c, String fieldUuid) {
        if (c == null || c.getRelationAttributes() == null || fieldUuid == null) return null;
        for (SemanticFieldSchemaDTO f : c.getRelationAttributes()) {
            if (f != null && fieldUuid.equals(f.getFieldUuid())) return f.getFieldName();
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
    private String resolveDictLabelOrOption(Long dictTypeId, Long fieldId, Object idOrVal, Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache, Map<Long, Map<String, String>> dictLabelCacheByFieldId) {
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
            List<SemanticFieldOptionDTO> options = fieldOptionsCache.get(fieldId);
            if (options == null || options.isEmpty()) return null;
            String valueStr = String.valueOf(idOrVal);
            for (SemanticFieldOptionDTO opt : options) {
                if (opt.getOptionValue() != null && opt.getOptionValue().equals(valueStr)) {
                    return opt.getOptionLabel();
                }
            }
        }
        return null;
    }

    /**
     * 构建字典显示标签缓存（按字段ID）
     *
     * <p>合并系统字典数据（id/value -> label），用于 SELECT/MULTI_SELECT 的显示名解析。</p>
     *
     * @param entity         实体Schema
     * @param selectFieldIds 需要解析的字典字段ID集合
     * @param fieldSchemaMap 字段Schema缓存
     * @return 字段ID到`(值->标签)`映射
     */
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
        
        if (log.isDebugEnabled()) { log.debug("dictDataBatch: {}", dictDataBatch); }
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

    /**
     * 统一解析上下文
     *
     * <p>聚合各类型所需的缓存：用户、部门、字段Schema与选项、字典标签、数据选择关系与主数据、文件。</p>
     */
    private static class ResolveContext {
        final Map<Long, AdminUserRespDTO> users;
        final Map<Long, DeptRespDTO> depts;
        final Map<Long, MetadataEntityFieldDO> fieldSchemaMap;
        final Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache;
        final Map<Long, Map<String, String>> dictLabelCacheByFieldId;
        final Map<String, DataSelectMeta> dataSelectMetaBySourceFieldUuid;
        final Map<String, Map<Object, Row>> mainsByTable;
        final Map<Long, FileListRespDTO> files;

        ResolveContext(Map<Long, AdminUserRespDTO> users,
                       Map<Long, DeptRespDTO> depts,
                       Map<Long, MetadataEntityFieldDO> fieldSchemaMap,
                       Map<Long, List<SemanticFieldOptionDTO>> fieldOptionsCache,
                       Map<Long, Map<String, String>> dictLabelCacheByFieldId,
                       Map<String, DataSelectMeta> dataSelectMetaBySourceFieldUuid,
                       Map<String, Map<Object, Row>> mainsByTable,
                       Map<Long, FileListRespDTO> files) {
            this.users = users;
            this.depts = depts;
            this.fieldSchemaMap = fieldSchemaMap;
            this.fieldOptionsCache = fieldOptionsCache;
            this.dictLabelCacheByFieldId = dictLabelCacheByFieldId;
            this.dataSelectMetaBySourceFieldUuid = dataSelectMetaBySourceFieldUuid;
            this.mainsByTable = mainsByTable;
            this.files = files;
        }
    }
}
