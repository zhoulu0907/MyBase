package com.cmsr.onebase.module.metadata.api.datamethod.impl;

import java.util.*;

import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.*;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.*;
import com.cmsr.onebase.module.metadata.core.enums.ClientTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.impl.MetadataEntityFieldCoreServiceImpl;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.query.MetadataQueryService;
import com.cmsr.onebase.module.metadata.api.datamethod.assembler.DataMethodAssembler;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

/**
 * 数据方法API实现类
 * 
 * 依赖 MetadataDataMethodCoreService，该服务有两个实现：
 * 1. MetadataDataMethodCoreServiceBuildImpl - 编辑态实现（在 build 模块）
 * 2. MetadataDataMethodCoreServiceImpl - 运行态实现（在 runtime 模块）
 * 
 * Spring会根据环境自动选择合适的实现：
 * - 在 onebase-server（编辑态）中使用 BuildImpl
 * - 在 onebase-server-runtime（运行态）中使用 RuntimeImpl
 * 
 * @author bty418
 * @date 2025-10-23
 */
@Service
@Slf4j
public class DataMethodApiImpl implements DataMethodApi {

    @Resource
    private MetadataQueryService metadataQueryService;

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private DataMethodAssembler dataMethodAssembler;

    @Resource
    private MetadataEntityFieldCoreServiceImpl metadataEntityFieldCoreServiceImpl;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;


    /**
     * 查询
     */
    @Override
    public List<List<EntityFieldDataRespDTO>> getDataByCondition(@Valid EntityFieldDataReqDTO reqDTO) {
        dataMethodAssembler.validateBase(reqDTO);
        log.info("开始根据条件查询数据，实体ID:{}", reqDTO.getEntityId());
        QueryRequest queryRequest = dataMethodAssembler.toQueryRequest(reqDTO);
        QueryResult queryResult = metadataQueryService.queryByConditions(queryRequest);
        return dataMethodAssembler.toResponseDTOs(queryResult);
    }

    /**
     * 删除
     */
    @Override
    public Integer deleteDataByCondition(@Valid DeleteDataReqDTO reqDTO) {
        log.info("开始根据条件删除数据，实体ID:{}", reqDTO.getEntityId());
        if (reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        if (reqDTO.getConditionDTO() == null || reqDTO.getConditionDTO().isEmpty()) {
            throw new IllegalArgumentException("删除操作必须提供条件，防止全表删除");
        }
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setEntityId(reqDTO.getEntityId());
        queryRequest.setConditionGroups(dataMethodAssembler.convertConditionGroups(reqDTO.getConditionDTO()));
        queryRequest.setLimit(1000);
        QueryResult result = metadataQueryService.queryByConditions(queryRequest);
        if (result.getRowDataList() == null || result.getRowDataList().isEmpty()) { return 0; }
        int success = 0;
        for (RowData row : result.getRowDataList()) {
            Object idObj = dataMethodAssembler.tryParseId(row.getRowId());
            try {
                MetadataDataMethodRequestContext metadataDataMethodRequestContext = new MetadataDataMethodRequestContext();
                metadataDataMethodRequestContext.setEntityId(reqDTO.getEntityId());
                metadataDataMethodRequestContext.setId(idObj);
                metadataDataMethodRequestContext.setMethodCode(null);
                metadataDataMethodRequestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.DELETE);
                metadataDataMethodRequestContext.setClientTypeEnum(ClientTypeEnum.BUILD);//编辑态调用
                metadataDataMethodRequestContext.setTraceId(reqDTO.getTraceId());
                Boolean ok = metadataDataMethodCoreService.deleteData(metadataDataMethodRequestContext);
                if (Boolean.TRUE.equals(ok)) { success++; }
            } catch (Exception e) {
                log.warn("删除失败 rowId:{} - {}", row.getRowId(), e.getMessage());
                throw exception(DB_OPERATION_ERROR_DELETE,e.getMessage());
            }
        }
        log.info("删除完成 成功:{} / 总:{}", success, result.getRowDataList().size());
        return success;
    }

    /**
     * 新增
     */
    @Override
    public List<List<EntityFieldDataRespDTO>> insertData(@Valid InsertDataReqDTO reqDTO) {
        log.info("开始插入数据，实体ID:{}", reqDTO.getEntityId());
        if (reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        if (reqDTO.getData() == null || reqDTO.getData().isEmpty()) {
            throw new IllegalArgumentException("插入数据内容不能为空");
        }
        List<Map<String, Object>> dataByNameList = dataMethodAssembler.convertIdKeyMapListToNameKeyMapList(
                reqDTO.getEntityId(), reqDTO.getData());
        if (dataByNameList.isEmpty()) {
            throw new IllegalArgumentException("插入数据内容不能为空");
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> dataByName : dataByNameList) {


            MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
            methodCoreContext.setEntityId(reqDTO.getEntityId());
            methodCoreContext.setData(dataByName);
            methodCoreContext.setMethodCode(null);
            methodCoreContext.setClientTypeEnum(ClientTypeEnum.BUILD);
            methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);
            methodCoreContext.setTraceId(reqDTO.getTraceId());

            try{
                Map<String, Object> resultMap = metadataDataMethodCoreService.createData(
                        methodCoreContext);
                if (resultMap != null) {
                    resultList.add(resultMap);
                }
            }catch (Exception e){
                log.warn("插入失败 data:{} - {}", dataByName, e.getMessage());
                throw exception(DB_OPERATION_ERROR_CREATE,e.getMessage());
            }

        }

        QueryResult qr = dataMethodAssembler.buildQueryResultFromCoreMultiResult(reqDTO.getEntityId(), resultList);
        return dataMethodAssembler.toResponseDTOs(qr);
    }

    /**
     * 更新
     */
    @Override
    public List<List<EntityFieldDataRespDTO>> updateData(@Valid UpdateDataReqDTO reqDTO) {
        log.info("开始更新数据，实体ID:{}", reqDTO.getEntityId());
        if (reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        if (reqDTO.getConditionDTO() == null || reqDTO.getConditionDTO().isEmpty()) {
            throw new IllegalArgumentException("更新操作必须提供条件，防止全表更新");
        }
        if (reqDTO.getData() == null || reqDTO.getData().isEmpty()) {
            throw new IllegalArgumentException("更新内容不能为空");
        }

        // 处理 实体和条件字段 参数不匹配
        if("subEntity".equals(reqDTO.getUpdateType())){
            Long subEntityId = reqDTO.getEntityId(); // 要修改的实体的实体id 此时为子表实体
            Long parentEntityId = reqDTO.getParentEntityId();// 子表对应的主表实体id
            
            // 获取实体UUID用于比较
            MetadataBusinessEntityDO subEntity = metadataBusinessEntityCoreService.getBusinessEntity(subEntityId);
            MetadataBusinessEntityDO parentEntity = metadataBusinessEntityCoreService.getBusinessEntity(parentEntityId);
            String subEntityUuid = subEntity.getEntityUuid();
            String parentEntityUuid = parentEntity.getEntityUuid();
            
            List<List<ConditionDTO>> conditionDTOs = reqDTO.getConditionDTO();
            for(List<ConditionDTO> list: conditionDTOs){
                for(ConditionDTO conditionDTO : list){
                    Long fieldId = conditionDTO.getFieldId();
                    //判断字段属于主表还是属于子表
                    boolean isMainEntityField = false;
                    MetadataEntityFieldDO metadataEntityFieldDO = metadataEntityFieldCoreServiceImpl.getEntityField(fieldId);
                    String fieldEntityUuid = metadataEntityFieldDO.getEntityUuid();
                    if(Objects.equals(fieldEntityUuid, parentEntityUuid)){
                        isMainEntityField = true;
                    }
                    if(isMainEntityField){
                        // 根据页面配置条件查询主表满足条件的数据行id
                        QueryRequest queryRequest = new QueryRequest();
                        queryRequest.setEntityId(reqDTO.getParentEntityId());

                        List<List<ConditionDTO>> newConditonsGroup = new ArrayList<>();
                        List<ConditionDTO> newConditons = new ArrayList<>();
                        newConditons.add(conditionDTO);
                        newConditonsGroup.add(newConditons);

                        queryRequest.setConditionGroups(dataMethodAssembler.convertConditionGroups(newConditonsGroup));
                        queryRequest.setLimit(1000);
                        QueryResult result = metadataQueryService.queryByConditions(queryRequest);

                        List<String> ids = new ArrayList<>();
                        List<RowData> rowDatas = result.getRowDataList();
                        if(ObjectUtils.isEmpty(rowDatas)){
                            ids.add(""); // 父表查询没有结果 sql拼接为 parent_id = NULL
                        }else{
                            for(RowData row : rowDatas){
                                ids.add(row.getRowId());// 父表查询有结果 sql拼接为 parent_id in (1,2,3)
                            }
                        }

                        // 重新组装条件（使用UUID版本的方法）
                        List<MetadataEntityRelationshipDO> relationshipDOS = entityRelationshipRepository.getRelationshipsBySlaveEntityUuid(subEntityUuid);
                        List<MetadataEntityRelationshipDO> relationships = relationshipDOS.stream()
                                .filter(relationship -> parentEntityUuid.equals(relationship.getSourceEntityUuid()))
                                .toList();
                        MetadataEntityRelationshipDO relationship = relationships.get(0); // 过滤之后只有一条关系
                        // 通过目标字段UUID获取字段ID（条件DTO仍使用字段ID）
                        String targetFieldUuid = relationship.getTargetFieldUuid();
                        MetadataEntityFieldDO targetField = metadataEntityFieldCoreServiceImpl.getEntityFieldByUuid(targetFieldUuid);
                        conditionDTO.setFieldId(targetField.getId());// 子表关联字段id
                        conditionDTO.setOperator(OpEnum.EXISTS_IN.name());// in
                        conditionDTO.setFieldValue(ids);// 主表id集合
                    }
                }
            }
        }


        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setEntityId(reqDTO.getEntityId());
        queryRequest.setConditionGroups(dataMethodAssembler.convertConditionGroups(reqDTO.getConditionDTO()));
        queryRequest.setLimit(1000);
        QueryResult result = metadataQueryService.queryByConditions(queryRequest);
        if (result.getRowDataList() == null || result.getRowDataList().isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> updateDataList = dataMethodAssembler.convertIdKeyMapListToNameKeyMapList(
                reqDTO.getEntityId(), reqDTO.getData());
        if (updateDataList.isEmpty()) {
            throw new IllegalArgumentException("更新内容不能为空");
        }

        List<RowData> rowDataList = result.getRowDataList();
        boolean singlePayload = updateDataList.size() == 1;
        if (!singlePayload && updateDataList.size() != rowDataList.size()) {
            throw new IllegalArgumentException("批量更新数据数量与匹配记录数量不一致");
        }

        List<Map<String, Object>> updatedList = new ArrayList<>();
        for (int i = 0; i < rowDataList.size(); i++) {
            RowData row = rowDataList.get(i);
            Object idObj = dataMethodAssembler.tryParseId(row.getRowId());
            try {
                Map<String, Object> payloadSource = singlePayload ? updateDataList.get(0) : updateDataList.get(i);
                Map<String, Object> payload = new HashMap<>(payloadSource);
                MetadataDataMethodRequestContext metadataDataMethodRequestContext = new MetadataDataMethodRequestContext();
                metadataDataMethodRequestContext.setEntityId(reqDTO.getEntityId());
                metadataDataMethodRequestContext.setId(idObj);
                metadataDataMethodRequestContext.setData(payload);
                metadataDataMethodRequestContext.setMethodCode(null);
                metadataDataMethodRequestContext.setClientTypeEnum(ClientTypeEnum.BUILD);//编辑态调用
                metadataDataMethodRequestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);
                metadataDataMethodRequestContext.setTraceId(reqDTO.getTraceId());
                Map<String, Object> updated = metadataDataMethodCoreService.updateData(
                        metadataDataMethodRequestContext);
                if (updated != null) {
                    updatedList.add(updated);
                }
            } catch (Exception e) {
                log.warn("更新失败 rowId:{} - {}", row.getRowId(), e.getMessage());
                throw exception(DB_OPERATION_ERROR_UPDATE,e.getMessage());
            }
        }
        QueryResult qr = dataMethodAssembler.buildQueryResultFromCoreMultiResult(reqDTO.getEntityId(), updatedList);
        return dataMethodAssembler.toResponseDTOs(qr);
    }
}

