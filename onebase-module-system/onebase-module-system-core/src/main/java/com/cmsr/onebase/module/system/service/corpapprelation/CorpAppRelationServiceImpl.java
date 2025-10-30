package com.cmsr.onebase.module.system.service.corpapprelation;


import com.cmsr.onebase.framework.common.enums.CorpReationStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.system.dal.database.CorpAppRelationDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.vo.corp.CorpApplicationRespVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
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
            corpAppRelationDO.setCorpId(createReqVO.getCorpId());
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
    /**
     * 获取app应用状态描述
     * @param
     * @return Map<Long, String> key为企业ID，value为企业名称
     */
    public String getCorpStatus(Integer status, LocalDateTime expiresTime) {
        String statusDesc = "";
        if (status != null && status.equals(CorpStatusEnum.DISABLE.getValue())) {
            statusDesc = CorpReationStatusEnum.DISABLE.getName();
        } else if (expiresTime != null) {
            if (expiresTime.isAfter(java.time.LocalDateTime.now())) {
                statusDesc = CorpReationStatusEnum.ENABLE.getName();
            } else {
                statusDesc = CorpReationStatusEnum.EXPIRES.getName();
            }
        }
        return statusDesc;
    }

    @Override
    public PageResult<CorpApplicationRespVO> getCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO) {
        // 查询原始分页数据
        PageResult<CorpAppRelationDO> pageResult = corpAppRelationDataRepository.selectPage(pageReqVO);
        Map<Long, ApplicationDO> applicationMap = getApplicationNameMap();

        // 转换为 VO 对象并根据 applicationName 过滤
        List<CorpApplicationRespVO> filteredList = pageResult.getList().stream()
                .map(corpDO -> {
                    CorpApplicationRespVO respVO = BeanUtils.toBean(corpDO, CorpApplicationRespVO.class);
                    ApplicationDO appDo = applicationMap.get(corpDO.getApplicationId());
                    if (appDo != null) {
                        respVO.setApplicationName(appDo.getAppName());
                        respVO.setApplicationCode(appDo.getAppCode());
                        respVO.setApplicationUid(appDo.getAppUid());
                        respVO.setApplicationId(appDo.getId());
                        respVO.setId(appDo.getId());
                        respVO.setVersionNumber(appDo.getVersionNumber());
                        // 获取app应用状态描述
                        Integer status = appDo.getAppStatus();
                        respVO.setStatusDesc(getCorpStatus(status, corpDO.getExpiresTime()));
                    }
                    return respVO;
                })
                // 添加 applicationName 过滤条件
                .filter(respVO -> {
                    // 如果未设置过滤条件，则不过滤
                    if (pageReqVO.getApplicationName() == null || pageReqVO.getApplicationName().trim().isEmpty()) {
                        return true;
                    }
                    // 比较应用名称是否匹配（不区分大小写）
                    return respVO.getApplicationName() != null &&
                            respVO.getApplicationName().toLowerCase().contains(
                                    pageReqVO.getApplicationName().toLowerCase().trim());
                })
                .collect(Collectors.toList());

        // 返回过滤后的结果和总数
        return new PageResult<>(filteredList, (long) filteredList.size());
    }
}

