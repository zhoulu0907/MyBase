package com.cmsr.onebase.module.metadata.runtime.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

/**
 * 运行时动态数据操作服务实现类
 * 作为适配器，调用core模块的基础服务并转换为VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class RuntimeDataServiceImpl implements RuntimeDataService {

    @Resource
    private MetadataDataMethodCoreService coreDataMethodService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    protected MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Override
    public DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO) {
        log.info("接收到创建数据请求，entityId: {}, 原始数据: {}, 子实体数据: {}", reqVO.getEntityId(), reqVO.getData(), reqVO.getSubEntities());

        // 将 field_id -> value 转换为 field_name -> value
        Map<String, Object> dataByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getData());

        log.info("字段ID映射为名称后的数据: {}", dataByName);

        // 打印每个字段值的类型
        dataByName.forEach((key, value) -> {
            if (value != null) {
                log.info("字段 {} 的值类型: {}, 值: {}", key, value.getClass().getName(), value);
            } else {
                log.info("字段 {} 的值为null", key);
            }
        });

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityId(reqVO.getEntityId());
        methodCoreContext.setData(dataByName);
        methodCoreContext.setMethodCode(reqVO.getMethodCode());
    // 运行时请求需要传递 menuId 给 core 层用于权限校验
    methodCoreContext.setMenuId(reqVO.getMenuId());
        methodCoreContext.setTraceId(reqVO.getTraceId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);

        if (CollectionUtils.isNotEmpty(reqVO.getSubEntities())) {
            List<MetadataDataMethodSubEntityContext> entityContexts = reqVO.getSubEntities().stream().map(item -> {
                MetadataDataMethodSubEntityContext metadataDataMethodSubEntityContext = new MetadataDataMethodSubEntityContext();
                metadataDataMethodSubEntityContext.setEntityId(item.getSubEntityId());
                metadataDataMethodSubEntityContext.setSubData(item.getSubData());
                return metadataDataMethodSubEntityContext;
            }).collect(Collectors.toList());
            methodCoreContext.setSubEntities(entityContexts);
        }


        // 调用core模块的基础服务
        Map<String, Object> resultData = coreDataMethodService.createData(
                methodCoreContext
        );

//        // 获取主表业务数据id，作为子表parent_id字段的值
//        Map<String,Object> map = (Map<String,Object>)resultData.get("data");
//        String parentId = (String)map.get("id");
//
//        // 处理子表插入数据
//        List<SubEntityVo> subEntities = reqVO.getSubEntities();
//        if(subEntities!=null){
//            for(SubEntityVo subEntityVo: subEntities){
//                //子实体Id
//                Long subEntityId = subEntityVo.getSubEntityId();
//                //该子实体对应多条数据待插入
//                List<Map<Long,Object>> list  = subEntityVo.getSubData();
//                for(Map<Long,Object> data: list){
//                    // 将 field_id -> value 转换为 field_name -> value
//                    Map<String, Object> subDataByName = convertIdKeyMapToNameKeyMap(subEntityId, data);
//
//                    subDataByName.put("parent_id",parentId);
//                    log.info("字段ID映射为名称后的数据: {}", subDataByName);
//
//                    // 打印每个字段值的类型
//                    subDataByName.forEach((key, value) -> {
//                        if (value != null) {
//                            log.info("字段 {} 的值类型: {}, 值: {}", key, value.getClass().getName(), value);
//                        } else {
//                            log.info("字段 {} 的值为null", key);
//                        }
//                    });
//
//                    MetadataDataMethodRequestContext subMethodCoreContext = new MetadataDataMethodRequestContext();
//                    subMethodCoreContext.setEntityId(subEntityId);
//                    subMethodCoreContext.setData(subDataByName);
//                    subMethodCoreContext.setMethodCode(reqVO.getMethodCode());
//                    methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);
//
//                    // 调用core模块的基础服务
//                    Map<String, Object> subResultData = coreDataMethodService.createData(
//                            subMethodCoreContext
//                    );
//                }
//            }
//        }

        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public DynamicDataRespVO updateData(DynamicDataUpdateReqVO reqVO) {
        // 将 field_id -> value 转换为 field_name -> value
        Map<String, Object> dataByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getData());

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityId(reqVO.getEntityId());
        methodCoreContext.setId(reqVO.getId());
        methodCoreContext.setData(dataByName);
        methodCoreContext.setMethodCode(reqVO.getMethodCode());
        methodCoreContext.setMenuId(reqVO.getMenuId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);

        if (CollectionUtils.isNotEmpty(reqVO.getSubEntities())) {
            List<MetadataDataMethodSubEntityContext> entityContexts = reqVO.getSubEntities().stream().map(item -> {
                MetadataDataMethodSubEntityContext metadataDataMethodSubEntityContext = new MetadataDataMethodSubEntityContext();
                metadataDataMethodSubEntityContext.setEntityId(item.getSubEntityId());
                metadataDataMethodSubEntityContext.setSubData(item.getSubData());
                return metadataDataMethodSubEntityContext;
            }).collect(Collectors.toList());
            methodCoreContext.setSubEntities(entityContexts);
        }

        // 调用core模块的基础服务 更新主表信息
        Map<String, Object> resultData = coreDataMethodService.updateData(methodCoreContext);

//        //查询关联关系 默认主表为source表
//        DefaultConfigStore configStore = new DefaultConfigStore();
//        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, reqVO.getEntityId());
//        configStore.order("create_time", Order.TYPE.DESC);
//        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
//        List<String> subTableIds = new ArrayList<String>();
//        //获取子表数据
//        List<SubEntityVo> subEntities = reqVO.getSubEntities();
//        for(MetadataEntityRelationshipDO relationshipDO:relationships){
//            //根据关联字段查询子表存在的所有记录
//            MetadataEntityFieldDO sourceFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getSourceFieldId()));
//
//            MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
//            MetadataEntityFieldDO targetFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getTargetFieldId()));
//            String tableName = targetEntity.getTableName();
//            String fieldName = targetFieldDO.getFieldName();
//            // 获取临时数据源服务
//            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(targetEntity.getDatasourceId());
//            if (datasource == null) {
//                throw exception(DATASOURCE_NOT_EXISTS);
//            }
//            AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
//            log.info("成功切换到数据源：{}", datasource.getCode());
//
//            DefaultConfigStore config = new DefaultConfigStore();
//            if("parent_id".equals(fieldName)){
//                config.and(fieldName, reqVO.getId());
//            }else{
//                Object value = dataByName.get(sourceFieldDO.getFieldName());
//                config.and(fieldName, value);
//            }
//            DataSet dataSet = temporaryService.querys(tableName,config);
//
//            //子表存在的数据行id
//            for (int i = 0; i < dataSet.size(); i++) {
//                DataRow row = dataSet.getRow(i);
//                subTableIds.add((String) row.get("id"));
//            }
//            //如果子表数据为空，对子表数据全部删除
//            if(subEntities.isEmpty()) {
//                List<MetadataEntityFieldDO> fields = entityFieldRepository.getEntityFieldListByEntityId(targetEntity.getId());
//                boolean hasDeletedField = fields.stream()
//                        .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));
//                DefaultConfigStore subConfig = new DefaultConfigStore();
//                subConfig.and("parent_id", reqVO.getId());
//                if (hasDeletedField) {
//                    // 软删除：更新deleted字段为删除时间戳
//                    DataRow updateData = new DataRow();
//                    updateData.put("deleted", String.valueOf(System.currentTimeMillis()));
//                    temporaryService.update(tableName, updateData, subConfig);
////                    log.info("软删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
//                } else {
//                     temporaryService.delete(tableName, subConfig);
////                    log.info("物理删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
//                }
////                for (String id : subTableIds) {
////                    coreDataMethodService.deleteData(
////                            targetEntity.getId(),
////                            id,
////                            null
////                    );
////                }
//            }
//        }
//
//        for(SubEntityVo subEntityVo: subEntities) {
//            //子实体Id
//            Long subEntityId = subEntityVo.getSubEntityId();
//
//            MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(subEntityId);
//
//
//            List<String> processedIds = new ArrayList<String>();
//            //该子实体对应多条数据待插入
//            List<Map<Long, Object>> list = subEntityVo.getSubData();
//
//            // 获取临时数据源服务
//            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(targetEntity.getDatasourceId());
//            if (datasource == null) {
//                throw exception(DATASOURCE_NOT_EXISTS);
//            }
//            AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
//
//            for (Map<Long, Object> data : list) {
//                Map<String, Object> subDataByName = convertIdKeyMapToNameKeyMap(subEntityId, data);
//                Object id = subDataByName.get("id");
//                if(id == null){
//                   //插入数据不包含id字段，说明数据表不存在则插入
//                    subDataByName.put("parent_id",reqVO.getId());
//
//                    MetadataDataMethodRequestContext metadataDataMethodRequestContext
//                            = new MetadataDataMethodRequestContext();
//                    metadataDataMethodRequestContext.setEntityId(subEntityId);
//                    metadataDataMethodRequestContext.setData(subDataByName);
//                    metadataDataMethodRequestContext.setMethodCode(reqVO.getMethodCode());
//                    methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);
//
//                   coreDataMethodService.createData(
//                           metadataDataMethodRequestContext
//                   );
//               }else{
//                   //插入数据包含id字段，说明数据表已经存在则修改
//                    subDataByName.remove("id");
//
//                    MetadataDataMethodRequestContext metadataDataMethodRequestContext = new MetadataDataMethodRequestContext();
//
//                    metadataDataMethodRequestContext.setEntityId(subEntityId);
//                    metadataDataMethodRequestContext.setId(id);
//                    metadataDataMethodRequestContext.setData(subDataByName);
//                    metadataDataMethodRequestContext.setMethodCode(reqVO.getMethodCode());
//                    metadataDataMethodRequestContext.setMenuId(reqVO.getMenuId());
//                    metadataDataMethodRequestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);
//
//                    if (CollectionUtils.isNotEmpty(reqVO.getSubEntities())) {
//                        List<MetadataDataMethodSubEntityContext> entityContexts = reqVO.getSubEntities().stream().map(item -> {
//                            MetadataDataMethodSubEntityContext metadataDataMethodSubEntityContext = new MetadataDataMethodSubEntityContext();
//                            metadataDataMethodSubEntityContext.setEntityId(item.getSubEntityId());
//                            metadataDataMethodSubEntityContext.setSubData(item.getSubData());
//                            return metadataDataMethodSubEntityContext;
//                        }).collect(Collectors.toList());
//                        methodCoreContext.setSubEntities(entityContexts);
//                    }
//
//                    coreDataMethodService.updateData(
//                            metadataDataMethodRequestContext
//                    );
//                    processedIds.add(id.toString());
//                }
//            }
//            //找出【在子表有的但没在更新信息表单】的记录行
//            List<String> toDelete = subTableIds.stream().filter(item ->
//                    !processedIds.contains(item)).collect(Collectors.toList());
//
//            //删除多余的【在子表有的但没在更新信息表单】数据行
//            for(String id: toDelete){
////                coreDataMethodService.deleteData(
////                        subEntityId,
////                        id,
////                        null
////                );
//
//                List<MetadataEntityFieldDO> fields = entityFieldRepository.getEntityFieldListByEntityId(subEntityId);
//                boolean hasDeletedField = fields.stream()
//                        .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));
//                DefaultConfigStore subConfig = new DefaultConfigStore();
//                subConfig.and("id", id);
//                if (hasDeletedField) {
//                    // 软删除：更新deleted字段为删除时间戳
//                    DataRow updateData = new DataRow();
//                    updateData.put("deleted", String.valueOf(System.currentTimeMillis()));
//                    temporaryService.update(targetEntity.getTableName(), updateData, subConfig);
////                    log.info("软删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
//                } else {
//                    temporaryService.delete(targetEntity.getTableName(), subConfig);
////                    log.info("物理删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
//                }
//
//
//
//            }
//        }

        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public Boolean deleteData(DynamicDataDeleteReqVO reqVO) {
        MetadataDataMethodRequestContext metadataDataMethodRequestContext = new MetadataDataMethodRequestContext();
        metadataDataMethodRequestContext.setEntityId(reqVO.getEntityId());
        metadataDataMethodRequestContext.setId(reqVO.getId());
        metadataDataMethodRequestContext.setMethodCode(reqVO.getMethodCode());
        metadataDataMethodRequestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.DELETE);
        metadataDataMethodRequestContext.setMenuId(reqVO.getMenuId());


        // 调用core模块的基础服务
        return coreDataMethodService.deleteData(
                metadataDataMethodRequestContext
        );
    }

    @Override
    public DynamicDataRespVO getData(DynamicDataGetReqVO reqVO) {
        // 调用core模块的基础服务查询主表数据
        Map<String, Object> resultData = coreDataMethodService.getData(
                reqVO.getEntityId(),
                reqVO.getId(),
                reqVO.getMethodCode(),
                reqVO.getMenuId()
        );

//        //查询子表数据
//        Long sourceEntityId = reqVO.getEntityId();
//        DefaultConfigStore configStore = new DefaultConfigStore();
//        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, sourceEntityId);
//        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
//        List<String> subTableIds = new ArrayList<String>();
//        for(MetadataEntityRelationshipDO relationshipDO:relationships){
//            MetadataEntityFieldDO sourceFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getSourceFieldId()));
//
//            MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
//            MetadataEntityFieldDO targetFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getTargetFieldId()));
//            String tableName = targetEntity.getTableName();
//            String fieldName = targetFieldDO.getFieldName();
//            // 获取临时数据源服务
//            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(targetEntity.getDatasourceId());
//            if (datasource == null) {
//                throw exception(DATASOURCE_NOT_EXISTS);
//            }
//            AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
//            log.info("成功切换到数据源：{}", datasource.getCode());
//
//            DefaultConfigStore config = new DefaultConfigStore();
//            if("parent_id".equals(fieldName)){
//                config.and(fieldName, reqVO.getId());
//            }else{
//                Object value = resultData.get(sourceFieldDO.getFieldName());
//                config.and(fieldName, value);
//            }
//            DataSet dataSet = temporaryService.querys(tableName,config);
//            System.out.println(dataSet);
//            }
        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO) {
        // 添加调试日志
        //--
        log.info("分页查询参数 - entityId: {}, pageNo: {}, pageSize: {}, pageSize类型: {}",
                reqVO.getEntityId(), reqVO.getPageNo(), reqVO.getPageSize(),
                reqVO.getPageSize() != null ? reqVO.getPageSize().getClass().getSimpleName() : "null");

        // 将 filters 的 field_id -> value 转换为 field_name -> value
        Map<String, Object> filtersByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getFilters());

        // 允许 sortField 传字段ID：如果是数字则转换为字段名
        String sortField = convertSortFieldToName(reqVO.getEntityId(), reqVO.getSortField());

        // 调用core模块的基础服务
        PageResult<Map<String, Object>> pageResult = coreDataMethodService.getDataPage(
                reqVO.getEntityId(),
                reqVO.getPageNo(),
                reqVO.getPageSize(),
                sortField,
                reqVO.getSortDirection(),
                filtersByName,
                reqVO.getMethodCode(),
                reqVO.getMenuId()
        );

        // 转换为VO
        List<DynamicDataRespVO> list = pageResult.getList().stream()
                .map(this::convertToDynamicDataRespVO)
                .collect(Collectors.toList());

        return new PageResult<>(list, pageResult.getTotal());
    }

    /**
     * 将Map数据转换为DynamicDataRespVO
     */
    @SuppressWarnings("unchecked")
    private DynamicDataRespVO convertToDynamicDataRespVO(Map<String, Object> data) {
        DynamicDataRespVO respVO = new DynamicDataRespVO();
        Object eid = data.get("entityId");
        if(eid instanceof String){
            try { respVO.setEntityId(Long.valueOf((String)eid)); } catch (NumberFormatException ex){ respVO.setEntityId(null); }
        } else if (eid instanceof Number){
            respVO.setEntityId(((Number) eid).longValue());
        }
        respVO.setEntityName((String) data.get("entityName"));
        respVO.setData((Map<String, Object>) data.get("data"));
        respVO.setFieldType((Map<String, String>) data.get("fieldType"));
        respVO.setSubEntities((List<SubEntityVo>)data.get("subEntities"));
        return respVO;

    }

    /**
     * 将以字段ID为键的Map转换为以字段名称为键的Map。
     * 对于无法匹配到字段名称的条目将被忽略，避免将无效字段传递到核心服务。
     *
     * @param entityId 实体ID
     * @param idKeyMap 以字段ID为key的Map
     * @return 以字段名称为key的Map
     */
    private Map<String, Object> convertIdKeyMapToNameKeyMap(Long entityId, Map<Long, Object> idKeyMap) {
        if (idKeyMap == null || idKeyMap.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        Map<Long, String> idToName = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldName() != null)
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName, (a,b) -> a));

        return idKeyMap.entrySet().stream()
                .filter(e -> e.getKey() != null && idToName.containsKey(e.getKey()))
                .collect(Collectors.toMap(e -> idToName.get(e.getKey()), Map.Entry::getValue, (a,b) -> b));
    }

    /**
     * 将排序字段支持ID或名称。若传入可解析为Long的ID，则转换为字段名称；否则原样返回。
     */
    private String convertSortFieldToName(Long entityId, String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return sortField;
        }
        try {
            Long id = Long.valueOf(sortField.trim());
            List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
            for (MetadataEntityFieldDO f : fields) {
                if (f.getId() != null && f.getId().equals(id)) {
                    return f.getFieldName();
                }
            }
        } catch (NumberFormatException ignore) {
            // 非数字，按名称处理
        }
        return sortField;
    }

}
