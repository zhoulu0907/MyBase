package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@Slf4j
public class MetadataDataMethodQueryServiceImpl implements MetadataDataMethodQueryService {

    @Resource
    private MetadataDataMethodService coreDataMethodService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        List<Map<String, Object>> methodList = coreDataMethodService.getEnabledDataMethodList(
            String.valueOf(queryVO.getEntityId()),
            queryVO.getMethodType(),
            queryVO.getKeyword()
        );
        
        List<DataMethodRespVO> methods = new ArrayList<>();
        for (Map<String, Object> method : methodList) {
            DataMethodRespVO vo = new DataMethodRespVO();
            vo.setMethodCode((String) method.get("methodCode"));
            vo.setMethodName((String) method.get("methodName"));
            vo.setMethodType((String) method.get("methodType"));
            vo.setDescription((String) method.get("description"));
            // TODO: 完善字段映射
            methods.add(vo);
        }
        return methods;
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        Map<String, Object> methodData = coreDataMethodService.getDataMethodByCode(
            String.valueOf(entityId),
            methodCode
        );
        
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        detail.setMethodCode((String) methodData.get("methodCode"));
        detail.setMethodName((String) methodData.get("methodName"));
        detail.setMethodType((String) methodData.get("methodType"));
        detail.setDescription((String) methodData.get("description"));
        // TODO: 完善字段映射和参数处理
        return detail;
    }
}
