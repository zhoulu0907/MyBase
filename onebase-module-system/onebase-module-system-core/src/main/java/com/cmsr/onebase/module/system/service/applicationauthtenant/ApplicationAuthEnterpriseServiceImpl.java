package com.cmsr.onebase.module.system.service.applicationauthtenant;


import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseInertReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import com.cmsr.onebase.module.system.convert.applicationauthtenant.ApplicationAuthEnterpriseConvert;
import com.cmsr.onebase.module.system.dal.database.ApplicationAuthEnterpriseDataRepository;
import com.cmsr.onebase.module.system.dal.database.EnterpriseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.EnterpriseDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.APPLICATION_AUTH_TENANT_NOT_EXISTS;

/**
 * 应用授权企业表 Service 实现类
 */
@Service
@Validated
@Slf4j
public class ApplicationAuthEnterpriseServiceImpl implements ApplicationAuthEnterpriseService {

    @Resource
    private ApplicationAuthEnterpriseDataRepository applicationAuthEnterpriseDataRepository;
    @Resource
    private EnterpriseDataRepository enterpriseDataRepository;

    @Override
    public void createApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseInertReqVO createReqVO) {
        // 插入
        createReqVO.getApplicationIdList().forEach(appliationId -> {
            createReqVO.setApplicationId(appliationId);
            ApplicationAuthEnterpriseDO applicationAuthEnterprise = ApplicationAuthEnterpriseConvert.INSTANCE.convert(createReqVO);
            applicationAuthEnterprise.setExpiresTime(java.time.LocalDateTime.now().plusYears(1));
            applicationAuthEnterpriseDataRepository.insert(applicationAuthEnterprise);
        });

        //获取企业应用数
        Long enterpriseId =  createReqVO.getEnterpriseId();
        Long authorizedAppsCount = applicationAuthEnterpriseDataRepository.countByEnterpriseId(enterpriseId);

        // 回写主表应用数
        DataRow row = new DataRow();
        row.put(EnterpriseDO.ID,  createReqVO.getEnterpriseId());
        row.put("authorized_apps", authorizedAppsCount);
        enterpriseDataRepository.updateByConfig(row, new DefaultConfigStore().eq(EnterpriseDO.ID, createReqVO.getEnterpriseId()));


    }

    @Override
    public void updateApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseInertReqVO updateReqVO) {

        // 更新
        ApplicationAuthEnterpriseDO updateObj = ApplicationAuthEnterpriseConvert.INSTANCE.convert(updateReqVO);
        applicationAuthEnterpriseDataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationAuthEnterprise(Long id) {
        // 校验存在
        validateApplicationAuthEnterpriseExists(id);
        // 删除
        applicationAuthEnterpriseDataRepository.deleteById(id);
    }

    private void validateApplicationAuthEnterpriseExists(Long id) {
        if (applicationAuthEnterpriseDataRepository.findById(id) == null) {
            throw exception(APPLICATION_AUTH_TENANT_NOT_EXISTS);
        }
    }

    @Override
    public ApplicationAuthEnterpriseVO getApplicationAuthEnterprise(Long id) {
        ApplicationAuthEnterpriseDO applicationAuthEnterpriseDO = applicationAuthEnterpriseDataRepository.findById(id);
        return ApplicationAuthEnterpriseConvert.INSTANCE.convert(applicationAuthEnterpriseDO);
    }

    @Override
    public PageResult<ApplicationAuthEnterpriseVO> getApplicationAuthEnterprisePage(ApplicationAuthEnterprisePageReqVO pageReqVO) {
        PageResult<ApplicationAuthEnterpriseDO> pageResult = applicationAuthEnterpriseDataRepository.selectPage(pageReqVO);
        // 将 DO 对象转换为 VO 对象
        return new PageResult<ApplicationAuthEnterpriseVO>(
                pageResult.getList().stream()
                        .map(enterpriseDO -> {
                            ApplicationAuthEnterpriseVO respVO = new ApplicationAuthEnterpriseVO();
                            respVO.setId(enterpriseDO.getId());
                            respVO.setApplicationId(enterpriseDO.getApplicationId());
                            respVO.setEnterpriseId(enterpriseDO.getEnterpriseId());

                            return respVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }
}