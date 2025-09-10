package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据方法查询 Service 实现类 - build模块专用
 * 作为适配器，调用core模块的基础服务并转换为VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class DataMethodQueryServiceImpl implements DataMethodQueryService {

    @Resource
    private MetadataDataMethodService coreDataMethodService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        // 调用core模块的基础服务
        List<Map<String, Object>> methodList = coreDataMethodService.getEnabledDataMethodList(
            queryVO.getEntityId(), 
            queryVO.getMethodType(), 
            queryVO.getKeyword()
        );

        // 转换为响应VO
        List<DataMethodRespVO> methods = new ArrayList<>();
        for (Map<String, Object> methodData : methodList) {
            DataMethodRespVO method = new DataMethodRespVO();
            method.setId((String) methodData.get("id"));
            method.setMethodName((String) methodData.get("methodName"));
            method.setMethodCode((String) methodData.get("methodCode"));
            method.setMethodType((String) methodData.get("methodType"));
            method.setUrl((String) methodData.get("url"));
            method.setHttpMethod((String) methodData.get("httpMethod"));
            method.setDescription((String) methodData.get("description"));
            // 设置输入输出参数（这里可以根据需要进一步扩展）
            method.setInputParameters(new ArrayList<>());
            method.setOutputParameters(new DataMethodOutputParameterVO());

            methods.add(method);
        }

        return methods;
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        // 调用core模块的基础服务
        Map<String, Object> methodData = coreDataMethodService.getDataMethodByCode(
            String.valueOf(entityId), 
            methodCode
        );

        // 转换为详情响应VO
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        detail.setMethodName((String) methodData.get("methodName"));
        detail.setMethodCode((String) methodData.get("methodCode"));
        detail.setMethodType((String) methodData.get("methodType"));
        detail.setUrl((String) methodData.get("url"));
        detail.setHttpMethod((String) methodData.get("httpMethod"));
        detail.setDescription((String) methodData.get("description"));

        // 设置输入输出参数（可以根据需要进一步完善）
        detail.setInputParameters(new ArrayList<>());
        detail.setOutputParameters(new DataMethodDetailOutputParameterVO());
        detail.setRequestExample(null);
        detail.setResponseExample(null);

        return detail;
    }
}