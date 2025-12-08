package com.cmsr.onebase.module.metadata.api.semantic.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dal.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticQueryConditionBuilder;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticValueAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetConditionVO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SemanticDynamicDataApiImpl implements SemanticDynamicDataApi {

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;
    @Resource
    private MetadataEntityFieldCoreService fieldCoreService;
    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;
    @Resource
    private SemanticPermissionContextLoader semanticPermissionContextLoader;
    @Resource
    private SemanticDataIntegrityValidator semanticDataIntegrityValidator;
    @Resource
    private SemanticPermissionValidator semanticPermissionValidator;
    @Resource
    private SemanticQueryConditionBuilder semanticQueryConditionBuilder;
    @Resource
    private SemanticDataCrudService semanticDataCrudService;
    @Resource
    private DynamicMetadataRepository dynamicMetadataRepository;
    @Resource
    private SemanticValueAssembler semanticValueAssembler;
    @Resource
    private com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticProcessLogger semanticProcessLogger;

    @Override
    public SemanticEntitySchemaDTO buildEntitySchemaByUuid(String entityUuid) {
        // 1) 按实体UUID构建实体模型
        return semanticMergeRecordAssembler.buildEntitySchemaByUuid(entityUuid);
    }

    @Override
    public SemanticEntitySchemaDTO buildEntitySchemaByTableName(String entityTableName) {
        // 1) 按主表名构建实体模型
        return semanticMergeRecordAssembler.buildEntitySchemaByTableName(entityTableName);
    }

    @Override
    public SemanticEntityValueDTO buildEntityValueByName(String tableName, Map<String, Object> fieldNameValues) {
        // 1) 通过表名与字段名-值直接构建实体值
        return semanticMergeRecordAssembler.buildEntityValueByNames(tableName, fieldNameValues);
    }

    @Override
    public SemanticEntityValueDTO buildEntityValueByUuid(String entityUuid, Map<String, Object> fieldUuidValues) {
        // 1) 读取实体字段列表
        List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityUuid(entityUuid);
        // 2) 构建字段UUID到字段名的映射
        Map<String, String> uuidToName = new HashMap<>();
        if (fields != null) {
            for (MetadataEntityFieldDO f : fields) {
                if (f.getFieldUuid() != null && f.getFieldName() != null) { uuidToName.put(f.getFieldUuid(), f.getFieldName()); }
            }
        }
        // 3) 将 uuid->value 转换为 name->value
        Map<String, Object> nameValues = new HashMap<>();
        if (fieldUuidValues != null) {
            for (Map.Entry<String, Object> e : fieldUuidValues.entrySet()) {
                String name = uuidToName.get(e.getKey());
                nameValues.put(name != null ? name : e.getKey(), e.getValue());
            }
        }
        // 4) 获取实体并校验
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByUuid(entityUuid);
        if (entity == null) { return null; }
        // 5) 通过表名与 name->value 构建实体值
        return buildEntityValueByName(entity.getTableName(), nameValues);
    }

    @Override
    public PageResult<SemanticEntityValueDTO> getDataByCondition(SemanticPageConditionVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return new PageResult<>(new ArrayList<>(), 0L); }
        // 1) 构建 RecordDTO（分页请求体与过滤条件）
        SemanticRecordDTO record = buildPageRecord(body);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性校验（内部调用忽略功能权限）
        semanticDataIntegrityValidator.validate(record);
        // 4) 构建查询条件（仅条件与排序，不应用数据权限）
        List<SemanticFieldSchemaDTO> fields = record.getEntitySchema().getFields();
        SemanticConditionDTO condition = record.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = record.getRecordContext().getSortBy();
        QueryWrapper qw = QueryWrapper.create();
        semanticQueryConditionBuilder.apply(qw, fields, condition, sortBy);
        // 5) 执行分页查询并转换结果
        PageResult<Map<String, Object>> page = semanticDataCrudService.queryPage(record, qw);
        List<SemanticEntityValueDTO> values = convertToValues(record.getEntitySchema(), page.getList());
        // 6) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return new PageResult<>(values, page.getTotal());
    }

    @Override
    public SemanticEntityValueDTO getDataById(SemanticTargetBodyVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return null; }
        // 1) 构建 RecordDTO（目标请求体）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, body, null, null,
                SemanticMethodCodeEnum.GET,
                SemanticDataMethodOpEnum.GET);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性校验
        semanticDataIntegrityValidator.validate(record);
        // 4) 查询详情数据
        semanticDataCrudService.readById(record);
        // 5) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return record.getResultValue();
    }

    @Override
    public Integer deleteDataById(SemanticTargetBodyVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return 0; }
        // 1) 构建 RecordDTO（目标请求体）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, body, null, null,
                SemanticMethodCodeEnum.DELETE,
                SemanticDataMethodOpEnum.DELETE);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性校验
        semanticDataIntegrityValidator.validate(record);
        // 4) 执行删除（软删优先）
        int resultCount = semanticDataCrudService.delete(record);
        // 5) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return resultCount;
    }

    @Override
    public Integer deleteDataByCondition(SemanticTargetConditionVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return 0; }
        // 1) 构建 RecordDTO（目标请求体 + 过滤条件）
        SemanticRecordDTO record = buildDeleteRecord(body);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性校验
        semanticDataIntegrityValidator.validate(record);
        // 4) 功能权限校验
        semanticPermissionValidator.validate(record);
        // 5) 条件安全校验，避免误删全表
        SemanticConditionDTO cond = body.getSemanticConditionDTO();
        boolean noCond = cond == null
                || (((cond.getFieldName() == null || cond.getFieldName().isBlank()) && cond.getFieldUuid() == null))
                || cond.getFieldValue() == null || cond.getFieldValue().isEmpty();
        if (noCond) { 
            throw new IllegalArgumentException("deleteDataByCondition: 为了避免删除全表数据，必须指定删除条件");
         }
        // 6) 构建条件查询包装器（仅条件，不应用数据权限）
        List<SemanticFieldSchemaDTO> fields = record.getEntitySchema().getFields();
        QueryWrapper qw = QueryWrapper.create();
        semanticQueryConditionBuilder.apply(qw, fields, cond, null);
        int affected = semanticDataCrudService.deleteByQuery(record, qw);
        // 7) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return affected;
    }

    @Override
    public List<SemanticEntityValueDTO> updateDataByCondition(SemanticTargetConditionVO body) {
        // 1) 参数校验
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return List.of(); }
        SemanticConditionDTO cond = body.getSemanticConditionDTO();
        boolean noCond = cond == null
                || (((cond.getFieldName() == null || cond.getFieldName().isBlank()) && cond.getFieldUuid() == null))
                || cond.getFieldValue() == null || cond.getFieldValue().isEmpty();
        Map<String, Object> updates = body.getUpdateProperties();
        if (noCond || updates == null || updates.isEmpty()) { return List.of(); }

        // 2) 构建 RecordDTO（目标请求体 + 过滤条件）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(tableName, new SemanticTargetBodyVO(), null, null,
                SemanticMethodCodeEnum.UPDATE,
                SemanticDataMethodOpEnum.UPDATE);
        record.getRecordContext().setFilters(cond);

        // 3) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);

        // 4) 数据完整性校验
        semanticDataIntegrityValidator.validate(record);

        // 5) 构建条件查询包装器（仅条件，不应用数据权限）
        List<SemanticFieldSchemaDTO> fields = record.getEntitySchema().getFields();
        QueryWrapper qw = QueryWrapper.create();
        semanticQueryConditionBuilder.apply(qw, fields, cond, null);
        List<Map<String, Object>> result = semanticDataCrudService.updateByQuery(record, updates, qw);

        // 6) 转换为语义值对象列表
        List<SemanticEntityValueDTO> values = convertToValues(record.getEntitySchema(), result);

        // 7) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return values;
    }

    @Override
    public SemanticEntityValueDTO insertData(SemanticMergeConditionVO body) {
        String tableName = null;
        if (body != null && body.getData() != null) {
            Object t = body.getTableName();
            if (t != null) { tableName = String.valueOf(t); }
        }
        if (tableName == null || tableName.isBlank()) { return null; }
        // 1) 构建 RecordDTO（合并请求体）
        SemanticMergeBodyVO mergeBody = new SemanticMergeBodyVO();
        if (body != null && body.getData() != null) {
            for (Map.Entry<String, Object> e : body.getData().entrySet()) {
                mergeBody.set(e.getKey(), e.getValue());
            }
        }
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, mergeBody, null, null,
                SemanticMethodCodeEnum.CREATE, SemanticDataMethodOpEnum.CREATE);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性与功能权限校验
        semanticDataIntegrityValidator.validate(record);
        // 4) 执行创建
        semanticDataCrudService.create(record);
        // 5) 按主键读取创建后的详情
        semanticDataCrudService.readById(record);
        // 6) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return record.getResultValue();
    }

    @Override
    public SemanticEntityValueDTO updateDataById(SemanticMergeConditionVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return null; }
        // 1) 构建 RecordDTO（合并请求体）
        SemanticMergeBodyVO mergeBody = new SemanticMergeBodyVO();
        if (body != null && body.getData() != null) {
            for (Map.Entry<String, Object> e : body.getData().entrySet()) {
                mergeBody.set(e.getKey(), e.getValue());
            }
        }
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, mergeBody, null, null,
                SemanticMethodCodeEnum.UPDATE, SemanticDataMethodOpEnum.UPDATE);
        // 2) 考虑后台调用，暂不初始化权限上下文初始化
        // semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性与功能权限校验
        semanticDataIntegrityValidator.validate(record);
        semanticPermissionValidator.validate(record);
        // 4) 执行更新
        semanticDataCrudService.update(record);
        // 5) 按主键读取更新后的详情
        semanticDataCrudService.readById(record);
        // 6) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return record.getResultValue();
    }

    @Override
    public List<SemanticFieldSchemaDTO> buildEntityFieldsSchemaByUuids(List<String> fieldUuids) {
        // 1) 初始化字段模型列表
        List<SemanticFieldSchemaDTO> list = new ArrayList<>();
        if (fieldUuids == null || fieldUuids.isEmpty()) { return list; }
        // 2) 遍历字段UUID列表并查询字段信息
        for (String uuid : fieldUuids) {
            MetadataEntityFieldDO f = fieldCoreService.getEntityFieldByUuid(uuid);
            if (f == null) { continue; }
            // 3) 构建字段模型并加入列表
            SemanticFieldSchemaDTO s = new SemanticFieldSchemaDTO();
            s.setId(f.getId());
            s.setFieldUuid(f.getFieldUuid());
            s.setFieldCode(f.getFieldCode());
            s.setFieldName(f.getFieldName());
            s.setDisplayName(f.getDisplayName());
            s.setFieldType(f.getFieldType());
            s.setDataLength(f.getDataLength());
            s.setDecimalPlaces(f.getDecimalPlaces());
            s.setIsRequired(f.getIsRequired() == null ? null : f.getIsRequired() == 1);
            s.setIsUnique(f.getIsUnique() == null ? null : f.getIsUnique() == 1);
            s.setIsSystemField(f.getIsSystemField() == null ? null : f.getIsSystemField() == 1);
            s.setIsPrimaryKey(f.getIsPrimaryKey() == null ? null : f.getIsPrimaryKey() == 1);
            s.setDictTypeId(f.getDictTypeId());
            list.add(s);
        }
        // 4) 返回字段模型列表
        return list;
    }

    private QueryWrapper buildPageQueryWrapper(SemanticRecordDTO recordDTO, QueryWrapper queryWrapper) {
        // 1) 初始化 QueryWrapper
        if (queryWrapper == null) { queryWrapper = QueryWrapper.create(); }
        // 2) 提取字段、过滤条件与排序规则
        List<SemanticFieldSchemaDTO> fields = recordDTO.getEntitySchema().getFields();
        SemanticConditionDTO condition = recordDTO.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = recordDTO.getRecordContext().getSortBy();
        // 3) 应用过滤与排序到查询包装器
        semanticQueryConditionBuilder.apply(queryWrapper, fields, condition, sortBy);
        // 4) 返回构建完成的 QueryWrapper
        return queryWrapper;
    }

    private SemanticRecordDTO buildPageRecord(SemanticPageConditionVO body) {
        // 1) 构建分页请求体 VO
        SemanticPageBodyVO pageBody = new SemanticPageBodyVO();
        // 2) 填充分页与排序参数
        pageBody.setPageNo(body.getPageNo());
        pageBody.setPageSize(body.getPageSize());
        pageBody.setSortBy(body.getSortBy());
        pageBody.setFilters(body.getSemanticConditionDTO());
        // 3) 组装 RecordDTO（包含权限上下文与操作类型）
        return semanticMergeRecordAssembler.assemblePageBody(body.getTableName(), pageBody, null, null,
                SemanticMethodCodeEnum.GET_PAGE, SemanticDataMethodOpEnum.GET_PAGE);
    }

    private List<SemanticEntityValueDTO> convertToValues(SemanticEntitySchemaDTO entity, List<Map<String, Object>> list) {
        // 1) 初始化结果集合
        List<SemanticEntityValueDTO> values = new ArrayList<>();
        if (list == null) { return values; }
        // 2) 遍历原始结果行并构建 Row
        for (Map<String, Object> m : list) {
            Row r = new Row();
            if (entity != null && entity.getFields() != null) {
                for (SemanticFieldSchemaDTO f : entity.getFields()) {
                    String name = f.getFieldName();
                    if (name != null && m.containsKey(name)) { r.put(name, m.get(name)); }
                }
            }
            // 3) 转换 Row 为语义值对象并加入结果
            values.add(semanticValueAssembler.toEntityValue(entity, r));
        }
        // 4) 返回语义值对象集合
        return values;
    }

    private SemanticRecordDTO buildDeleteRecord(SemanticTargetConditionVO body) {
        // 1) 构建目标请求体 VO
        SemanticTargetBodyVO target = new SemanticTargetBodyVO();
        // 2) 组装 RecordDTO（包含操作类型与权限上下文）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(body.getTableName(), target, null, null,
                SemanticMethodCodeEnum.DELETE,
                SemanticDataMethodOpEnum.DELETE);
        // 3) 设置过滤条件到上下文
        record.getRecordContext().setFilters(body.getSemanticConditionDTO());
        // 4) 返回构建完成的 RecordDTO
        return record;
    }

    

    
}
