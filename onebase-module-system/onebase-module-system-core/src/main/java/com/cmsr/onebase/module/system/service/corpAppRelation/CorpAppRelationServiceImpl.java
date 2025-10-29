package com.cmsr.onebase.module.system.service.corpAppRelation;


import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.system.dal.database.CorpAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationUpdateReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.APPLICATION_AUTH_TENANT_NOT_EXISTS;

/**
 * 企业应用关联表 Service 实现类
 */
@Service
@Validated
@Slf4j
public class CorpAppRelationServiceImpl implements CorpAppRelationService {

    @Resource
    private CorpAppRelationDataRepository corpAppRelationDataRepository;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Override
    public void createCorpAppRelation(@Valid CorpAppRelationInertReqVO createReqVO) {

        // 先删除后插入
        ConfigStore configs = new DefaultConfigStore();
        configs.in("application_id", createReqVO.getApplicationIdList());
        configs.eq("corp_id", createReqVO.getCorpId());
        configs.eq("tenant_id", TenantContextHolder.getRequiredTenantId());
        corpAppRelationDataRepository.deleteByConfig(configs);

        // 插入
        createReqVO.getApplicationIdList().forEach(appliationId -> {
            // 验证是否重复提交，先删除后插入
            CorpAppRelationDO corpAppRelationDO = BeanUtils.toBean(createReqVO, CorpAppRelationDO.class);
            corpAppRelationDO.setApplicationId(appliationId);
            corpAppRelationDO.setAuthorizationTime(createReqVO.getAuthorizationTime());
            corpAppRelationDO.setExpiresTime(createReqVO.getAuthorizationTime().plusYears(CorpConstant.EXPIRESYEAR));
            corpAppRelationDO.setStatus(CorpStatusEnum.ENABLE.getValue());
            corpAppRelationDataRepository.insert(corpAppRelationDO);
        });
    }

    @Override
    public void updateCorpAppRelation(@Valid CorpAppRelationUpdateReqVO updateReqVO) {
        // 更新
        CorpAppRelationDO updateObj = BeanUtils.toBean(updateReqVO, CorpAppRelationDO.class);
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
        return BeanUtils.toBean(applicationAuthEnterpriseDO, CorpAppRelationVO.class);
    }

    /**
     * 获取应用名称
     *
     * @param
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    private Map<Long, ApplicationDO> getApplicationNameMap() {
        List<ApplicationDO> pageDOList = appApplicationApi.finAppApplicationAll();
        return pageDOList.stream()
                .collect(Collectors.toMap(
                        ApplicationDO::getId,
                        Function.identity()
                ));
    }

    @Override
    public PageResult<CorpAppRelationVO> getCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO) {
        PageResult<CorpAppRelationDO> pageResult = corpAppRelationDataRepository.selectPage(pageReqVO);
        Map<Long, ApplicationDO> applicationMap = getApplicationNameMap();
        // 将 DO 对象转换为 VO 对象
        return new PageResult<CorpAppRelationVO>(
                pageResult.getList().stream()
                        .map(corpDO -> {
                            CorpAppRelationVO respVO = BeanUtils.toBean(corpDO, CorpAppRelationVO.class);
                            ApplicationDO appDo = applicationMap.get(corpDO.getApplicationId());
                            if (appDo != null) {
                                respVO.setApplicationName(appDo.getAppName());
                                respVO.setApplicationCode(appDo.getAppCode());
                                respVO.setVersionNumber(appDo.getVersionNumber());
                            }
                            return respVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }
}