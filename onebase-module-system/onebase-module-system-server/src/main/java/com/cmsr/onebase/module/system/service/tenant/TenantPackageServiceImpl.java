package com.cmsr.onebase.module.system.service.tenant;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.TENANT_PACKAGE_DISABLE;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.TENANT_PACKAGE_NAME_DUPLICATE;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.TENANT_PACKAGE_NOT_EXISTS;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.TENANT_PACKAGE_USED;

import java.util.List;

import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.packages.TenantPackageSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.google.common.annotations.VisibleForTesting;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;

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
    private DataRepository dataRepository;

    @Override
    public Long createTenantPackage(TenantPackageSaveReqVO createReqVO) {
        // 校验套餐名是否重复
        validateTenantPackageNameUnique(null, createReqVO.getName());
        // 插入
        TenantPackageDO tenantPackage = BeanUtils.toBean(createReqVO, TenantPackageDO.class);
//        tenantPackageMapper.insert(tenantPackage);
        dataRepository.insert(tenantPackage);
        // 返回
        return tenantPackage.getId();
    }

    @Override
    public void updateTenantPackage(TenantPackageSaveReqVO updateReqVO) {
        // 校验存在
        TenantPackageDO tenantPackage = validateTenantPackageExists(updateReqVO.getId());
        // 校验套餐名是否重复
        validateTenantPackageNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 更新
        TenantPackageDO updateObj = BeanUtils.toBean(updateReqVO, TenantPackageDO.class);
//        tenantPackageMapper.updateById(updateObj);
        dataRepository.update(updateObj);
        // 如果菜单发生变化，则修改每个租户的菜单
        if (!CollUtil.isEqualList(tenantPackage.getMenuIds(), updateReqVO.getMenuIds())) {
            List<TenantDO> tenants = tenantService.getTenantListByPackageId(tenantPackage.getId());
            tenants.forEach(tenant -> tenantService.updateTenantRoleMenu(tenant.getId(), updateReqVO.getMenuIds()));
        }
    }

    @Override
    public void deleteTenantPackage(Long id) {
        // 校验存在
        validateTenantPackageExists(id);
        // 校验正在使用
        validateTenantUsed(id);
        // 删除
//        tenantPackageMapper.deleteById(id);
        dataRepository.deleteById(TenantPackageDO.class, id);
    }

    private TenantPackageDO validateTenantPackageExists(Long id) {
//        TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
        TenantPackageDO tenantPackage = dataRepository.findById(TenantPackageDO.class, id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
        return tenantPackage;
    }

    private void validateTenantUsed(Long id) {
        if (tenantService.getTenantCountByPackageId(id) > 0) {
            throw exception(TENANT_PACKAGE_USED);
        }
    }

    @Override
    public TenantPackageDO getTenantPackage(Long id) {
        return dataRepository.findById(TenantPackageDO.class, id);
//        return tenantPackageMapper.selectById(id);
    }

    @Override
    public PageResult<TenantPackageDO> getTenantPackagePage(TenantPackagePageReqVO pageReqVO) {
        return dataRepository.findPageWithConditions(TenantPackageDO.class, new DefaultConfigStore().order("id", Order.TYPE.DESC),
        pageReqVO.getPageNo(),pageReqVO.getPageSize());
//        return tenantPackageMapper.selectPage(pageReqVO);
    }

    @Override
    public TenantPackageDO validTenantPackage(Long id) {
//        TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
        TenantPackageDO tenantPackage = dataRepository.findById(TenantPackageDO.class, id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
        if (tenantPackage.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_PACKAGE_DISABLE, tenantPackage.getName());
        }
        return tenantPackage;
    }

    @Override
    public List<TenantPackageDO> getTenantPackageListByStatus(Integer status) {
//        return tenantPackageMapper.selectListByStatus(status);
        return dataRepository.findAll(TenantPackageDO.class, new DefaultConfigStore().eq("status", status));
    }


    @VisibleForTesting
    void validateTenantPackageNameUnique(Long id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }
//        TenantPackageDO tenantPackage = tenantPackageMapper.selectByName(name);
        TenantPackageDO tenantPackage = dataRepository.findOne(TenantPackageDO.class, new DefaultConfigStore().eq("name", name));
        if (tenantPackage == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
        if (!tenantPackage.getId().equals(id)) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
    }

}
