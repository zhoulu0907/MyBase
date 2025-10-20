package com.cmsr.onebase.module.system.service.enterprise;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseRespVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.EnterpriseDO;
import com.cmsr.onebase.module.system.dal.database.EnterpriseDataRepository;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


/**
 * 企业服务实现类
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Service
@Validated
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {

    @Resource
    private EnterpriseDataRepository enterpriseDataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEnterprise(EnterpriseSaveReqVO reqVO) {
        EnterpriseDO enterprise =  BeanUtils.toBean(reqVO, EnterpriseDO.class);
        enterprise.setLockVersion(0L);
        enterprise.setTenantId(reqVO.getTenantId());
        enterprise.setCreateTime(java.time.LocalDateTime.now());
        enterprise.setUpdateTime(java.time.LocalDateTime.now());
        enterprise.setDeleted(0L);
        return enterpriseDataRepository.insert(enterprise).getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterprise(EnterpriseSaveReqVO reqVO) {
        EnterpriseDO enterprise =  BeanUtils.toBean(reqVO, EnterpriseDO.class);
        enterprise.setUpdateTime(java.time.LocalDateTime.now());
        enterpriseDataRepository.update(enterprise);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(Long id) {
        EnterpriseDO enterprise = new EnterpriseDO();
        enterprise.setId(id);
        enterprise.setDeleted(1L);
        enterprise.setUpdateTime(java.time.LocalDateTime.now());
        enterpriseDataRepository.update(enterprise);
    }

    @Override
    public PageResult<EnterpriseRespVO> getEnterprisePage(EnterprisePageReqVO pageReqVO) {


        // 调用数据仓库进行分页查询
        PageResult<EnterpriseDO> pageResult = enterpriseDataRepository.selectPage(pageReqVO);

        // 将 DO 对象转换为 VO 对象
        return new PageResult<EnterpriseRespVO>(
                pageResult.getList().stream()
                        .map(enterpriseDO -> {
                            EnterpriseRespVO enterpriseRespVO=   BeanUtils.toBean(enterpriseDO, EnterpriseRespVO.class);
                            return enterpriseRespVO;
                        })
                        .collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal()
        );
    }

    @Override
    public EnterpriseRespVO getEnterprise(Long id) {
        EnterpriseDO enterprise = enterpriseDataRepository.findById(id);
        if (enterprise == null) {
            return null;
        }

        EnterpriseRespVO respVO=   BeanUtils.toBean(enterprise, EnterpriseRespVO.class);
        return respVO;
    }
}
