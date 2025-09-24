package com.cmsr.onebase.module.metadata.api.datamethod.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.assembler.DataMethodAssembler;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
import com.cmsr.onebase.module.metadata.core.service.query.MetadataQueryService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
/**
 * 数据方法API实现类
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Service
@Slf4j
public class DataMethodApiImpl implements DataMethodApi {

    @Resource
    private MetadataQueryService metadataQueryService;
    
    @Resource
    private DataMethodAssembler dataMethodAssembler;

    /**
     * 根据条件查询数据
     * 
     * @param reqDTO 查询请求DTO
     * @return 查询结果二维列表（外层List表示多行数据，内层List表示一行的所有字段数据）
     */
    @Override
    public List<List<EntityFieldDataRespDTO>> getDataByCondition(@Valid EntityFieldDataReqDTO reqDTO) {
        log.info("开始根据条件查询数据，实体ID: {}, 条件数量: {}", 
                 reqDTO.getEntityId(), 
                 reqDTO.getConditionDTO() != null ? reqDTO.getConditionDTO().size() : 0);

        // 1. 校验请求参数
        validateRequest(reqDTO);

        // 2. 转换DTO为领域模型
        QueryRequest queryRequest = dataMethodAssembler.toQueryRequest(reqDTO);

        // 3. 执行领域查询
        QueryResult queryResult = metadataQueryService.queryByConditions(queryRequest);

        // 4. 转换领域结果为响应DTO（按行组织）
        List<List<EntityFieldDataRespDTO>> responseDTOs = dataMethodAssembler.toResponseDTOs(queryResult);

        log.info("查询完成，共{}行数据", responseDTOs.size());
        return responseDTOs;
    }

    /**
     * 校验请求参数
     * 
     * @param reqDTO 请求DTO
     */
    private void validateRequest(EntityFieldDataReqDTO reqDTO) {
        if (reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
    }

    @Override
    public Integer deleteDataByCondition(@Valid DeleteDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDataByCondition'");
    }

    @Override
    public List<List<EntityFieldDataRespDTO>> insertData(@Valid InsertDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertData'");
    }

    @Override
    public List<List<EntityFieldDataRespDTO>> updateData(@Valid UpdateDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateData'");
    }
}
