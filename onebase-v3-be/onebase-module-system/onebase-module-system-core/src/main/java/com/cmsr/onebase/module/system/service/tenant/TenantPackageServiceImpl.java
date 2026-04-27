package com.cmsr.onebase.module.system.service.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.vo.tenant.TenantPackagePageReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantPackageSaveReqVO;
import com.cmsr.onebase.module.system.dal.database.TenantPackageDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 租户套餐 Service 实现类
 *
 */
@Service
@Validated
public class TenantPackageServiceImpl implements TenantPackageService {

    @Resource
    @Lazy // 避免循环依赖的报错
    private TenantService tenantService;

    @Resource
    private TenantPackageDataRepository tenantPackageDataRepository;

    @Override
    public Long createTenantPackage(TenantPackageSaveReqVO createReqVO) {
        // 校验套餐名是否重复
        validateTenantPackageNameUnique(null, createReqVO.getName());
        // 插入
        TenantPackageDO tenantPackage = BeanUtils.toBean(createReqVO, TenantPackageDO.class);
        tenantPackageDataRepository.insert(tenantPackage);
        return tenantPackage.getId();
    }

    @Override
    public void updateTenantPackage(TenantPackageSaveReqVO updateReqVO) {
        // 校验存在
        validateTenantPackageExists(updateReqVO.getId());
        // 校验套餐名是否重复
        validateTenantPackageNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 更新
        TenantPackageDO updateObj = BeanUtils.toBean(updateReqVO, TenantPackageDO.class);
        tenantPackageDataRepository.update(updateObj);
    }

    @Override
    public void deleteTenantPackage(Long id) {
        // 校验存在
        validateTenantPackageExists(id);
        // 校验正在使用
        if (tenantService.getTenantCountByPackageId(id) > 0) {
            throw exception(TENANT_PACKAGE_USED);
        }
        // 删除
        tenantPackageDataRepository.deleteById(id);
    }

    private void validateTenantPackageExists(Long id) {
        if (tenantPackageDataRepository.findById(id) == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
    }

    @Override
    public TenantPackageDO getTenantPackage(Long id) {
        return tenantPackageDataRepository.findById(id);
    }

    @Override
    public PageResult<TenantPackageDO> getTenantPackagePage(TenantPackagePageReqVO pageReqVO) {
        return tenantPackageDataRepository.findPage(pageReqVO);
    }

    @Override
    public TenantPackageDO validTenantPackage(Long id) {
        TenantPackageDO tenantPackage = tenantPackageDataRepository.findById(id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
        if (tenantPackage.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_PACKAGE_DISABLE);
        }
        return tenantPackage;
    }

    @Override
    public List<TenantPackageDO> getTenantPackageListByStatus(Integer status) {
        return tenantPackageDataRepository.findListByStatus(status);
    }

    @Override
    public TenantPackageDO getTenantPackageByCode(String code) {
        return tenantPackageDataRepository.findOneByCode(code);
    }

    @VisibleForTesting
    void validateTenantPackageNameUnique(Long id, String name) {
        TenantPackageDO tenantPackage = tenantPackageDataRepository.findOneByName(name);
        if (tenantPackage == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的租户套餐
        if (id == null) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
        if (!tenantPackage.getId().equals(id)) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
    }

}
