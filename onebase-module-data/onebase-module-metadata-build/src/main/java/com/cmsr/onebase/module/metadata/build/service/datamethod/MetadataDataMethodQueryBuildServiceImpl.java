package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.build.service.datamethod.vo.DataMethodQueryVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataSystemMethodCoreService;
import com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 构建端 - 数据方法查询服务实现
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Service
@Slf4j
public class MetadataDataMethodQueryBuildServiceImpl implements MetadataDataMethodQueryBuildService {

    @Resource
    private MetadataDataMethodCoreService coreDataMethodService; // 保留对运行时动态操作依赖

    @Resource
    private MetadataDataSystemMethodCoreService metadataDataSystemMethodCoreService;

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        // 校验实体存在（优先使用entityUuid）
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(queryVO.getEntityUuid());
        if (entity == null) {
            throw exception(ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取启用的数据方法列表
        List<MetadataDataSystemMethodDO> methodDOList = metadataDataSystemMethodCoreService.getEnabledDataMethodList();
        return methodDOList.stream().map(methodDO -> {
            DataMethodRespVO vo = new DataMethodRespVO();
            vo.setMethodCode(methodDO.getMethodCode());
            vo.setMethodName(methodDO.getMethodName());
            vo.setMethodType(methodDO.getMethodType());
            vo.setDescription(methodDO.getMethodDescription());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(String entityUuid, String methodCode) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(entityUuid);
        if (entity == null) {
            throw exception(ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS);
        }
        MetadataDataSystemMethodDO methodDO = metadataDataSystemMethodCoreService.getDataMethodByCode(methodCode);
        if (methodDO == null) {
            throw exception(ErrorCodeConstants.DATA_METHOD_NOT_EXISTS);
        }
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        detail.setMethodCode(methodDO.getMethodCode());
        detail.setMethodName(methodDO.getMethodName());
        detail.setMethodType(methodDO.getMethodType());
        detail.setDescription(methodDO.getMethodDescription());
        return detail;
    }
}
