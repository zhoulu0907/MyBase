package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.build.service.datamethod.vo.DataMethodQueryVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataSystemMethodCoreService;
import com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;

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
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(queryVO.getEntityId());
        if (entity == null) {
            throw exception(ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS);
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataDataSystemMethodDO.IS_ENABLED, CommonStatusEnum.ENABLE.getStatus());
        configStore.and("deleted", 0);
        if (StringUtils.hasText(queryVO.getMethodType())) {
            configStore.and(MetadataDataSystemMethodDO.METHOD_TYPE, queryVO.getMethodType());
        }
        if (StringUtils.hasText(queryVO.getKeyword())) {
            configStore.and(MetadataDataSystemMethodDO.METHOD_NAME, "%" + queryVO.getKeyword() + "%", "like");
        }
        configStore.order(MetadataDataSystemMethodDO.METHOD_CODE, Order.TYPE.ASC);

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
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
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
