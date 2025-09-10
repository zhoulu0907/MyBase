package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运行时动态数据操作服务实现类
 * 作为适配器，调用core模块的基础服务并转换为VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
public class RuntimeDataServiceImpl implements RuntimeDataService {

    @Resource
    private MetadataDataMethodService coreDataMethodService;

    @Override
    public DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO) {
        // 调用core模块的基础服务
        Map<String, Object> resultData = coreDataMethodService.createData(
            reqVO.getEntityId(), 
            reqVO.getData(), 
            reqVO.getMethodCode()
        );
        
        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public DynamicDataRespVO updateData(DynamicDataUpdateReqVO reqVO) {
        // 调用core模块的基础服务
        Map<String, Object> resultData = coreDataMethodService.updateData(
            reqVO.getEntityId(), 
            reqVO.getId(), 
            reqVO.getData(), 
            reqVO.getMethodCode()
        );
        
        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public Boolean deleteData(DynamicDataDeleteReqVO reqVO) {
        // 调用core模块的基础服务
        return coreDataMethodService.deleteData(
            reqVO.getEntityId(), 
            reqVO.getId(), 
            reqVO.getMethodCode()
        );
    }

    @Override
    public DynamicDataRespVO getData(DynamicDataGetReqVO reqVO) {
        // 调用core模块的基础服务
        Map<String, Object> resultData = coreDataMethodService.getData(
            reqVO.getEntityId(), 
            reqVO.getId(), 
            reqVO.getMethodCode()
        );
        
        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO) {
        // 调用core模块的基础服务
        PageResult<Map<String, Object>> pageResult = coreDataMethodService.getDataPage(
            reqVO.getEntityId(), 
            reqVO.getPageNo(), 
            reqVO.getPageSize(), 
            reqVO.getSortField(), 
            reqVO.getSortDirection(), 
            reqVO.getFilters(), 
            reqVO.getMethodCode()
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
        respVO.setEntityId((String) data.get("entityId"));
        respVO.setEntityName((String) data.get("entityName"));
        respVO.setData((Map<String, Object>) data.get("data"));
        respVO.setFieldType((Map<String, String>) data.get("fieldType"));
        return respVO;

    }
}