package com.cmsr.onebase.module.system.service.corpAppRelation;


import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationVO;
import com.cmsr.onebase.module.system.convert.applicationauthtenant.CorpAppRelationConvert;
import com.cmsr.onebase.module.system.dal.database.CorpAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.database.CorpDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.APPLICATION_AUTH_TENANT_NOT_EXISTS;

/**
 * 企业应用关联表 Service 实现类
 */
@Service
@Validated
@Slf4j
public class CorpAppRelationServiceImpl implements  CorpAppRelationService   {

    @Resource
    private CorpAppRelationDataRepository corpAppRelationDataRepository;

    @Override
    public void createCorpAppRelation(@Valid CorpAppRelationInertReqVO createReqVO) {

        // 先删除后插入
        ConfigStore configs = new DefaultConfigStore();
        configs.in("application_id", createReqVO.getApplicationIdList());
        configs.eq("corp_id",createReqVO.getCorpId());
        configs.eq("tenant_id", TenantContextHolder.getRequiredTenantId());
        corpAppRelationDataRepository.deleteByConfig(configs);

        // 插入
        createReqVO.getApplicationIdList().forEach(appliationId -> {
            // 验证是否重复提交，先删除后插入
            CorpAppRelationDO corpAppRelationDO = CorpAppRelationConvert.INSTANCE.convert(createReqVO);
            corpAppRelationDO.setApplicationId(appliationId);
            corpAppRelationDO.setAuthorizationTime(createReqVO.getAuthorizationTime());
            corpAppRelationDO.setExpiresTime(createReqVO.getAuthorizationTime().plusYears(1));
            corpAppRelationDataRepository.insert(corpAppRelationDO);
        });
    }

    @Override
    public void updateCorpAppRelation(@Valid CorpAppRelationInertReqVO updateReqVO) {

        // 更新
        CorpAppRelationDO updateObj = CorpAppRelationConvert.INSTANCE.convert(updateReqVO);
        corpAppRelationDataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCorpAppRelation(Long id) {
        // 校验存在
        validateCorpAppRelationExists(id);
        // 删除
        corpAppRelationDataRepository.deleteById(id);
    }

    private void validateCorpAppRelationExists(Long id) {
        if (corpAppRelationDataRepository.findById(id) == null) {
            throw exception(APPLICATION_AUTH_TENANT_NOT_EXISTS);
        }
    }

    @Override
    public CorpAppRelationVO getCorpAppRelation(Long id) {
        CorpAppRelationDO applicationAuthEnterpriseDO = corpAppRelationDataRepository.findById(id);
        return CorpAppRelationConvert.INSTANCE.convert(applicationAuthEnterpriseDO);
    }

    @Override
    public PageResult<CorpAppRelationVO> getCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO) {
        PageResult<CorpAppRelationDO> pageResult = corpAppRelationDataRepository.selectPage(pageReqVO);
        // 将 DO 对象转换为 VO 对象
        return new PageResult<CorpAppRelationVO>(
                pageResult.getList().stream()
                        .map(corpDO -> {
                            CorpAppRelationVO respVO = new CorpAppRelationVO();
                            respVO=   BeanUtils.toBean(corpDO, CorpAppRelationVO.class);
                            return respVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }
}