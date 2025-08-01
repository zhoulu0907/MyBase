package com.cmsr.onebase.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.datapermission.core.annotation.DataPermission;
import com.cmsr.onebase.framework.tenant.config.TenantProperties;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.mysql.tenant.TenantMapper;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantInfoHandler;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantMenuHandler;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;

/**
 * 租户 Service 实现类
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 yudao.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private DataRepository dataRepository;
    @Resource
    private LicenseService licenseService;


    @Override
    public List<Long> getTenantIdList() {
//        List<TenantDO> tenants = tenantMapper.selectList();
        List<TenantDO> tenants = dataRepository.findAll(TenantDO.class);
        return CollectionUtils.convertList(tenants, TenantDO::getId);
    }

    @Override
    public void validTenant(Long id) {
        TenantDO tenant = getTenant(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_DISABLE, tenant.getName());
        }
        if (DateUtils.isExpired(tenant.getExpireTime())) {
            throw exception(TENANT_EXPIRE, tenant.getName());
        }
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    @DataPermission(enable = false) // 参见 https://gitee.com/zhijiantianya/onebase_v3/pulls/1154 说明
    public Long createTenant(TenantSaveReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        if (createReqVO.getWebsite() != null) {
        validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        }
        // 校验套餐被禁用
        createReqVO.setPackageId(112L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse("2099-02-19 00:00:00", formatter);
        createReqVO.setExpireTime(expireTime);
        createReqVO.setAccountCount(100);
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(createReqVO.getPackageId());
        LicenseDO license = licenseService.getLicenseByStatus("enable");
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总人数限制
            Integer licenseTotalCount = license.getTenantLimit();
            // 获取已分配人员数量
            Long allocatedCount = userService.getUserCountByStatus(0);

            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (createReqVO.getAllocatePersonCount()!= null &&
                    (allocatedCount + createReqVO.getAllocatePersonCount()) > licenseTotalCount) {

                Long remainingCount = licenseTotalCount - allocatedCount;
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH,
                        licenseTotalCount,
                        remainingCount);
            }
        }
        
        // 校验联系人用户名是否已存在
        if (StringUtils.isNotEmpty(createReqVO.getContactName())) {
            TenantUtils.execute(createReqVO.getId(), () -> {
                if (userService.getUserByUsername(createReqVO.getContactName()) != null) {
                    throw exception(USER_USERNAME_EXISTS, createReqVO.getContactName());
                }
            });
        }
        
        // 创建租户
        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
//        tenantMapper.insert(tenant);
        tenant = dataRepository.insert(tenant);
        // 创建租户的管理员1
        TenantDO finalTenant = tenant;
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建角色
            Long roleId = createRole(tenantPackage);
            // 创建用户，并分配角色
            createReqVO.setUsername(createReqVO.getContactName());
            if (StringUtils.isEmpty(createReqVO.getPassword())) {
                createReqVO.setPassword("admin123");
            }
            Long userId = createUser(roleId, createReqVO);
            // 修改租户的管理员
//            tenantMapper.updateById(new TenantDO().setId(tenant.getId()).setContactUserId(userId));
            TenantDO tenantDO = new TenantDO().setContactUserId(userId);
            tenantDO.setId(finalTenant.getId());
            dataRepository.update(tenantDO);
        });
        return tenant.getId();
    }

    private Long createUser(Long roleId, TenantSaveReqVO createReqVO) {
        // 创建用户
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert02(createReqVO));
        // 分配角色
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageDO tenantPackage) {
        // 创建角色
        RoleSaveReqVO reqVO = new RoleSaveReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        // 分配权限
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public void updateTenant(TenantSaveReqVO updateReqVO) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());
        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        if (updateReqVO.getWebsite() != null) {
            validTenantWebsiteDuplicate(updateReqVO.getWebsite(), updateReqVO.getId());
        }
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(updateReqVO.getPackageId());
        //根据管理员名称和tenant_id去查询，当前修改的管理员名称是否存在
        AdminUserDO user = userService.getUserByTenantIDAndUserName(updateReqVO.getContactName(), updateReqVO.getId());
        TenantUtils.execute(tenant.getId(), () -> {
            if (user != null) {
                throw exception(USER_USERNAME_EXISTS);
            }
        });
        // 如果联系人不为空，根据tenant_id和username去查询，判断是否为空，不为空则异常，租户管理员名称已存在
        if (StringUtils.isNotEmpty(updateReqVO.getContactName())) {
                if (userService.getUserByUsername(updateReqVO.getContactName()) != null) {
                    throw exception(USER_USERNAME_EXISTS);
                }
        }

        LicenseDO license = licenseService.getLicenseByStatus("enable");
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总人数限制
            Integer licenseTotalCount = license.getTenantLimit();
            // 获取已分配人员数量
            Long allocatedCount = userService.getUserCountByStatus(0);

            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (updateReqVO.getAllocatePersonCount()!= null &&
                    (allocatedCount + updateReqVO.getAllocatePersonCount()) > licenseTotalCount) {

                Long remainingCount = licenseTotalCount - allocatedCount;
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH,
                        licenseTotalCount,
                        remainingCount);
            }
            if (updateReqVO.getAllocatePersonCount() < allocatedCount) {
                throw exception(LENANT_ALLOCATE_PERSON_COUNT_LESS_THEN_ALLOCATED,
                        allocatedCount);
            }

        }

        // 更新租户
        TenantDO updateObj = BeanUtils.toBean(updateReqVO, TenantDO.class);
        // tenantMapper.updateById(updateObj);
        dataRepository.update(updateObj);
        
        // 如果套餐发生变化，则修改其角色的权限
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateReqVO.getPackageId())) {
            updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
//        TenantDO tenant = tenantMapper.selectByName(name);
        TenantDO tenant = dataRepository.findOne(TenantDO.class, new DefaultConfigStore().eq("name", name));

        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (id == null) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
    }

    private void validTenantWebsiteDuplicate(String website, Long id) {
        if (StrUtil.isEmpty(website)) {
            return;
        }
//        TenantDO tenant = tenantMapper.selectByWebsite(website);
        TenantDO tenant = dataRepository.findOne(TenantDO.class, new DefaultConfigStore().eq("website", website));

        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (id == null) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            // 获得所有角色
            List<RoleDO> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "角色({}/{}) 租户不匹配",
                    role.getId(), role.getTenantId(), tenantId)); // 兜底校验
            // 重新分配每个角色的权限
            roles.forEach(role -> {
                // 如果是租户管理员，重新分配其权限为租户套餐的权限
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), menuIds);
                    return;
                }
                // 如果是其他角色，则去掉超过套餐的权限
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Override
    public void deleteTenant(Long id) {
        // 校验存在
        validateUpdateTenant(id);
        // 删除
//        tenantMapper.deleteById(id);
        dataRepository.deleteById(TenantDO.class, id);
    }

    private TenantDO validateUpdateTenant(Long id) {
//        TenantDO tenant = tenantMapper.selectById(id);
        TenantDO tenant = dataRepository.findById(TenantDO.class, id);

        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
        if (isSystemTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantDO getTenant(Long id) {
//        return tenantMapper.selectById(id);
        return dataRepository.findById(TenantDO.class, id);
    }

    @Override
    public PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
        return dataRepository.findPageWithConditions(TenantDO.class, new DefaultConfigStore(), pageReqVO.getPageNo(), pageReqVO.getPageSize());
//        return tenantMapper.selectPage(pageReqVO);
    }

    @Override
    public TenantDO getTenantByName(String name) {
        return dataRepository.findOne(TenantDO.class, new DefaultConfigStore().eq("name", name));
//        return tenantMapper.selectByName(name);
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
//        return tenantMapper.selectByWebsite(website);
        return dataRepository.findOne(TenantDO.class, new DefaultConfigStore().eq("website", website));

    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return dataRepository.countByConfig(TenantDO.class, new DefaultConfigStore().eq("package_id", packageId));
//        return tenantMapper.selectCountByPackageId(packageId);
    }

    @Override
    public Long getTenantCountByStatus(Integer status) {
        return dataRepository.countByConfig(TenantDO.class, new DefaultConfigStore().eq("status", status));
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return dataRepository.findAllByConfig(TenantDO.class, new DefaultConfigStore().eq("package_id", packageId));
//        return tenantMapper.selectListByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByStatus(Integer status) {
        return dataRepository.findAllByConfig(TenantDO.class, new DefaultConfigStore().eq("status", status));
//        return tenantMapper.selectListByStatus(status);
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // 执行处理器
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户，然后获得菜单
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantDO tenant) {
        return Objects.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
