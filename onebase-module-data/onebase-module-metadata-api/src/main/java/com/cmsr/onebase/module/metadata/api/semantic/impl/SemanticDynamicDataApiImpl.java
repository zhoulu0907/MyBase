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
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionContextLoader;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticPermissionValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticDataIntegrityValidator;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.permission.SemanticQueryPermissionHelper;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticQueryConditionBuilder;
import com.cmsr.onebase.module.metadata.core.semantic.service.SemanticDataCrudService;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticValueAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicTargetConditionVO;
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
    private SemanticQueryPermissionHelper semanticQueryPermissionHelper;
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
        return semanticMergeRecordAssembler.buildEntitySchemaByUuid(entityUuid);
    }

    @Override
    public SemanticEntitySchemaDTO buildEntitySchemaByTableName(String entityTableName) {
        return semanticMergeRecordAssembler.buildEntitySchemaByTableName(entityTableName);
    }

    @Override
    public SemanticEntityValueDTO buildEntityValueByName(String tableName, Map<String, Object> fieldNameValues) {
        return semanticMergeRecordAssembler.buildEntityValueByNames(tableName, fieldNameValues);
    }

    @Override
    public SemanticEntityValueDTO buildEntityValueByUuid(String entityUuid, Map<String, Object> fieldUuidValues) {
        List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityUuid(entityUuid);
        Map<String, String> uuidToName = new HashMap<>();
        if (fields != null) {
            for (MetadataEntityFieldDO f : fields) {
                if (f.getFieldUuid() != null && f.getFieldName() != null) { uuidToName.put(f.getFieldUuid(), f.getFieldName()); }
            }
        }
        Map<String, Object> nameValues = new HashMap<>();
        if (fieldUuidValues != null) {
            for (Map.Entry<String, Object> e : fieldUuidValues.entrySet()) {
                String name = uuidToName.get(e.getKey());
                nameValues.put(name != null ? name : e.getKey(), e.getValue());
            }
        }
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByUuid(entityUuid);
        if (entity == null) { return null; }
        return buildEntityValueByName(entity.getTableName(), nameValues);
    }

    @Override
    public PageResult<SemanticEntityValueDTO> getDataByCondition(SemanticPageConditionVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return new PageResult<>(new ArrayList<>(), 0L); }
        // 1) 构建 RecordDTO（分页请求体与过滤条件）
        SemanticRecordDTO record = buildPageRecord(body);
        // 2) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性，因为内部逻辑流调用，忽略功能权限校验
        semanticDataIntegrityValidator.validate(record);
        // 4) 构建查询条件（权限过滤 + 过滤/排序）
        QueryWrapper qw = buildPageQuery(record);
        // 5) 执行分页查询并转换结果
        PageResult<Map<String, Object>> page = semanticDataCrudService.queryPage(record, qw);
        List<SemanticEntityValueDTO> values = convertToValues(record.getEntitySchema(), page.getList());
        // 6) 执行分页查询并转换结果
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
                MetadataDataMethodOpEnum.GET);
        // 2) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性
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
                MetadataDataMethodOpEnum.DELETE);
        // 2) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
        // 3) 数据完整性
        semanticDataIntegrityValidator.validate(record);
        // 4) 执行删除（软删优先）
        int resultCount = semanticDataCrudService.delete(record);
        // 5) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return resultCount;
    }

    @Override
    public Integer deleteDataByCondition(SemanicTargetConditionVO body) {
        String tableName = body == null ? null : body.getTableName();
        if (tableName == null || tableName.isBlank()) { return 0; }
        SemanticRecordDTO record = buildDeleteRecord(body);
        semanticPermissionContextLoader.loadPermissionContext(record);
        semanticDataIntegrityValidator.validate(record);
        semanticPermissionValidator.validate(record);
        SemanticConditionDTO cond = body.getSemanticConditionDTO();
        boolean noCond = cond == null
                || (((cond.getFieldName() == null || cond.getFieldName().isBlank()) && cond.getFieldUuid() == null))
                || cond.getFieldValue() == null || cond.getFieldValue().isEmpty();
        if (noCond) { 
            throw new IllegalArgumentException("deleteDataByCondition: 为了避免删除全表数据，必须指定删除条件");
         }
        int affected = semanticDataCrudService.deleteByCondition(record);
        semanticProcessLogger.log(record);
        return affected;
    }

    @Override
    public List<SemanticEntityValueDTO> updateDataByCondition(SemanicTargetConditionVO body) {
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
                MetadataDataMethodOpEnum.UPDATE);
        record.getRecordContext().setFilters(cond);

        // 3) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);

        // 4) 数据完整性
        semanticDataIntegrityValidator.validate(record);

        // 5) 调用服务层：条件更新并返回更新后的结果列表（Map）
        List<Map<String, Object>> result = semanticDataCrudService.updateByCondition(record, updates);

        // 6) 转换为语义值对象列表
        List<SemanticEntityValueDTO> values = convertToValues(record.getEntitySchema(), result);

        // 7) 记录过程日志并返回
        semanticProcessLogger.log(record);
        return values;
    }

    @Override
    public SemanticEntityValueDTO insertData(SemanticMergeBodyVO body) {
        String tableName = null;
        if (body != null && body.any() != null) {
            Object t = body.any().get("tableName");
            if (t != null) { tableName = String.valueOf(t); }
        }
        if (tableName == null || tableName.isBlank()) { return null; }
        // 1) 构建 RecordDTO（合并请求体）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, body, null, null,
                SemanticMethodCodeEnum.CREATE, MetadataDataMethodOpEnum.CREATE);
        // 2) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
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
    public SemanticEntityValueDTO updateDataById(SemanticMergeBodyVO body) {
        String tableName = null;
        if (body != null && body.any() != null) {
            Object t = body.any().get("tableName");
            if (t != null) { tableName = String.valueOf(t); }
        }
        if (tableName == null || tableName.isBlank()) { return null; }
        // 1) 构建 RecordDTO（合并请求体）
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleMergeBody(tableName, body, null, null,
                SemanticMethodCodeEnum.UPDATE, MetadataDataMethodOpEnum.UPDATE);
        // 2) 权限上下文初始化
        semanticPermissionContextLoader.loadPermissionContext(record);
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
    public List<SemanticFieldSchemaDTO> buildEntityFieldsSchemaByTableName(List<String> fieldUuids) {
        List<SemanticFieldSchemaDTO> list = new ArrayList<>();
        if (fieldUuids == null || fieldUuids.isEmpty()) { return list; }
        for (String uuid : fieldUuids) {
            MetadataEntityFieldDO f = fieldCoreService.getEntityFieldByUuid(uuid);
            if (f == null) { continue; }
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
        return list;
    }

    private QueryWrapper buildPageQueryWrapper(SemanticRecordDTO recordDTO, QueryWrapper queryWrapper) {
        if (queryWrapper == null) { queryWrapper = QueryWrapper.create(); }
        List<SemanticFieldSchemaDTO> fields = recordDTO.getEntitySchema().getFields();
        SemanticConditionDTO condition = recordDTO.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = recordDTO.getRecordContext().getSortBy();
        semanticQueryConditionBuilder.apply(queryWrapper, fields, condition, sortBy);
        return queryWrapper;
    }

    private SemanticRecordDTO buildPageRecord(SemanticPageConditionVO body) {
        SemanticPageBodyVO pageBody = new SemanticPageBodyVO();
        pageBody.setPageNo(body.getPageNo());
        pageBody.setPageSize(body.getPageSize());
        pageBody.setSortBy(body.getSortBy());
        pageBody.setFilters(body.getSemanticConditionDTO());
        return semanticMergeRecordAssembler.assemblePageBody(body.getTableName(), pageBody, null, null,
                SemanticMethodCodeEnum.GET_PAGE, MetadataDataMethodOpEnum.GET_PAGE);
    }

    private QueryWrapper buildPageQuery(SemanticRecordDTO record) {
        QueryWrapper qw = semanticQueryPermissionHelper.applyQueryPermissionFilter(null,
                record.getRecordContext().getPermissionContext(),
                record.getEntitySchema().getFields());
        return buildPageQueryWrapper(record, qw);
    }

    private List<SemanticEntityValueDTO> convertToValues(SemanticEntitySchemaDTO entity, List<Map<String, Object>> list) {
        List<SemanticEntityValueDTO> values = new ArrayList<>();
        if (list == null) { return values; }
        for (Map<String, Object> m : list) {
            Row r = new Row();
            if (entity != null && entity.getFields() != null) {
                for (SemanticFieldSchemaDTO f : entity.getFields()) {
                    String name = f.getFieldName();
                    if (name != null && m.containsKey(name)) { r.put(name, m.get(name)); }
                }
            }
            values.add(semanticValueAssembler.toEntityValue(entity, r));
        }
        return values;
    }

    private SemanticRecordDTO buildDeleteRecord(SemanicTargetConditionVO body) {
        SemanticTargetBodyVO target = new SemanticTargetBodyVO();
        SemanticRecordDTO record = semanticMergeRecordAssembler.assembleTargetBody(body.getTableName(), target, null, null,
                SemanticMethodCodeEnum.DELETE,
                MetadataDataMethodOpEnum.DELETE);
        record.getRecordContext().setFilters(body.getSemanticConditionDTO());
        return record;
    }

    

    
}
