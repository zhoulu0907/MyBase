package com.cmsr.onebase.module.system.service.applicationauthtenant;



import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import com.cmsr.onebase.module.system.convert.applicationauthtenant.ApplicationAuthEnterpriseConvert;
import com.cmsr.onebase.module.system.dal.database.ApplicationAuthEnterpriseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public Long createApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseSaveReqVO createReqVO) {
        // 插入
        ApplicationAuthEnterpriseDO applicationAuthEnterprise = ApplicationAuthEnterpriseConvert.INSTANCE.convert(createReqVO);
        applicationAuthEnterpriseDataRepository.insert(applicationAuthEnterprise);
        // 返回
        return applicationAuthEnterprise.getId();
    }

    @Override
    public void updateApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseSaveReqVO updateReqVO) {

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