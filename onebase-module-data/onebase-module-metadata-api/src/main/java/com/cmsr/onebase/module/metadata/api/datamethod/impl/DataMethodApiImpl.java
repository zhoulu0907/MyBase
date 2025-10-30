package com.cmsr.onebase.module.metadata.api.datamethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodCoreContext;
import com.cmsr.onebase.module.metadata.core.enums.ClientTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
import com.cmsr.onebase.module.metadata.core.domain.query.RowData;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.query.MetadataQueryService;
import com.cmsr.onebase.module.metadata.api.datamethod.assembler.DataMethodAssembler;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据方法API实现类（运行态）
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
                MetadataDataMethodCoreContext metadataDataMethodCoreContext = new MetadataDataMethodCoreContext();
                metadataDataMethodCoreContext.setEntityId(reqDTO.getEntityId());
                metadataDataMethodCoreContext.setId(idObj);
                metadataDataMethodCoreContext.setMethodCode(null);
                metadataDataMethodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.DELETE);
                metadataDataMethodCoreContext.setClientTypeEnum(ClientTypeEnum.BUILD);//编辑态调用

                Boolean ok = metadataDataMethodCoreService.deleteData(metadataDataMethodCoreContext);
                if (Boolean.TRUE.equals(ok)) { success++; }
            } catch (Exception e) {
                log.warn("删除失败 rowId:{} - {}", row.getRowId(), e.getMessage());
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


            MetadataDataMethodCoreContext methodCoreContext = new MetadataDataMethodCoreContext();
            methodCoreContext.setEntityId(reqDTO.getEntityId());
            methodCoreContext.setData(dataByName);
            methodCoreContext.setMethodCode(null);
            methodCoreContext.setClientTypeEnum(ClientTypeEnum.BUILD);
            methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);


            Map<String, Object> resultMap = metadataDataMethodCoreService.createData(
                    methodCoreContext);
            if (resultMap != null) {
                resultList.add(resultMap);
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
                MetadataDataMethodCoreContext metadataDataMethodCoreContext = new MetadataDataMethodCoreContext();
                metadataDataMethodCoreContext.setEntityId(reqDTO.getEntityId());
                metadataDataMethodCoreContext.setId(idObj);
                metadataDataMethodCoreContext.setData(payload);
                metadataDataMethodCoreContext.setMethodCode(null);
                metadataDataMethodCoreContext.setClientTypeEnum(ClientTypeEnum.BUILD);//编辑态调用
                metadataDataMethodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);

                Map<String, Object> updated = metadataDataMethodCoreService.updateData(
                        metadataDataMethodCoreContext);
                if (updated != null) {
                    updatedList.add(updated);
                }
            } catch (Exception e) {
                log.warn("更新失败 rowId:{} - {}", row.getRowId(), e.getMessage());
            }
        }
        QueryResult qr = dataMethodAssembler.buildQueryResultFromCoreMultiResult(reqDTO.getEntityId(), updatedList);
        return dataMethodAssembler.toResponseDTOs(qr);
    }
}

