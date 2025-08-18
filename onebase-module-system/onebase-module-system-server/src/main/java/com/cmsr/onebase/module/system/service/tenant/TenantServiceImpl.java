package com.cmsr.onebase.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.config.TenantProperties;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.database.AdminUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.TenantDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.PackageTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantStatusEnum;
import com.cmsr.onebase.module.system.enums.user.UserPasswordEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantInfoHandler;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantMenuHandler;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    @Resource // 由于 yudao.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private AdminUserService     userService;
    @Resource
    private RoleService          roleService;
    @Resource
    private MenuService          menuService;
    @Resource
    private PermissionService    permissionService;
    @Resource
    private LicenseService       licenseService;
    @Resource
    private AppApplicationApi    appApplicationApi;

    @Resource
    private TenantDataRepository tenantDataRepository;

    @Resource
    private AdminUserDataRepository adminUserDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    @Resource
    private RoleDataRepository roleDataRepository;

    @Override
    public List<Long> getTenantIdList() {
        List<TenantDO> tenants = tenantDataRepository.findAll();
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
    public Long getAvailableAccountCount() {
        LicenseDO license = licenseService.getLicenseByStatus(LicenseStatusEnum.ENABLE.getStatus());
        Integer userCount = userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        if (license != null) {
            // 获取license总人数限制
            Integer licenseUserLimit = license.getUserLimit();
            // 如果license总人数限制小于已分配人员数量，则返回0
            if (userCount > licenseUserLimit) {
                return 0L;
            }
            // 返回剩余可分配人员数量
            return (long) (licenseUserLimit - userCount);
        }
        return 0L;
    }

    @Override
    public Long getOtherTenantUserLimitCount(Long tenantId) {
        List<TenantDO> tenantDOList = tenantDataRepository.findAllByStatusExcludePlatform(
                TenantStatusEnum.NORMAL.getStatus(), tenantId);
        long sum = tenantDOList.stream()
                .filter(tenantDO -> tenantDO.getAccountCount() != null)
                .mapToLong(TenantDO::getAccountCount)
                .sum();
        return sum;
    }

    @Override
    public Long getTenantExistUserCount(Long tenantId) {
        // 查询当前租户下已分配的用户数量
        return TenantUtils.execute(tenantId, () -> {
            return Long.valueOf(userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus()));
        });
    }

    @Override
    public Long createTenant(TenantInsertReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        if (StringUtils.isNotEmpty(createReqVO.getWebsite())) {
            validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        }
        if (createReqVO.getPackageId() == null) {
            createReqVO.setPackageId(Long.valueOf(PackageTypeEnum.SIMPLE.getPackageId()));
        }
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(createReqVO.getPackageId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse("2099-02-19 00:00:00", formatter);
        if (createReqVO.getExpireTime() == null) {
            createReqVO.setExpireTime(expireTime);
        }

        LicenseDO license = licenseService.getLicenseByStatus(LicenseStatusEnum.ENABLE.getStatus());
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总租户数限制
            Integer totalTenantLimit = license.getTenantLimit();
            // 获取现有租户数量
            Long existTenantCount = getExistTenantCount();
            if (existTenantCount >= totalTenantLimit) {
                throw exception(LICENSE_TENANT_COUNT_NOT_ENOUGH);
            }

            // 获取license总人数限制
            Integer totalUserLimit = license.getUserLimit();

            // 获取现有租户已分配的用户数量
            Long otherUserCount = getOtherTenantUserLimitCount(null);
            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (otherUserCount + createReqVO.getAccountCount() > totalUserLimit) {
                Integer remainingCount = (int) (totalUserLimit - otherUserCount);
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH, totalUserLimit, remainingCount);
            }
        }

        // 创建租户
        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
        tenant = tenantDataRepository.insert(tenant);

        // 创建租户的管理员1
        TenantDO finalTenant = tenant;
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建角色
            Long roleId = createRole(tenantPackage);
            // 创建用户，并分配角色
            createReqVO.setUsername(createReqVO.getContactName());
            createReqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
            if (StringUtils.isEmpty(createReqVO.getPassword())) {
                createReqVO.setPassword(UserPasswordEnum.PASSWORD_ENUM.getPassword());
            }
            Long userId = createUser(roleId, createReqVO);
            // 修改租户的管理员
            TenantDO tenantDO = new TenantDO().setContactUserId(userId);
            tenantDO.setId(finalTenant.getId());
            tenantDataRepository.update(tenantDO);
        });
        return tenant.getId();
    }

    private Long getExistTenantCount() {
        // 排除平台租户
        Long existTenantCount = tenantDataRepository.countByStatusExcludePlatform(
                TenantStatusEnum.NORMAL.getStatus(), null);
        return existTenantCount;
    }

    private Long createUser(Long roleId, TenantInsertReqVO createReqVO) {
        // 创建用户
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert02(createReqVO));
        // 分配角色
        permissionService.assignUserRoles(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageDO tenantPackage) {
        // 创建角色
        RoleInsertReqVO reqVO = new RoleInsertReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        // 分配权限
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    @Override
    public void updateTenant(@Valid TenantUpdateReqVO updateReqVO) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());
        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        if (StringUtils.isNotBlank(updateReqVO.getWebsite())) {
            validTenantWebsiteDuplicate(updateReqVO.getWebsite(), updateReqVO.getId());
        }
        // 校验套餐被禁用
        Long packageId = updateReqVO.getPackageId();
        if (packageId == null) {
            packageId = tenant.getPackageId();
        }
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(packageId);

        LicenseDO license = licenseService.getLicenseByStatus(LicenseStatusEnum.ENABLE.getStatus());
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总人数限制
            Integer userLimit = license.getUserLimit();
            // 获取除当前租户外的其他租户已分配人员数量，判断上限
            Long otherUserCount = getOtherTenantUserLimitCount(updateReqVO.getId());
            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (updateReqVO.getAccountCount() != null &&
                    (otherUserCount + updateReqVO.getAccountCount()) > userLimit) {

                Integer remainingCount = (int) (userLimit - otherUserCount);
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH,
                        userLimit,
                        remainingCount);
            }
            TenantUtils.execute(tenant.getId(), () -> {
                // 查询当前租户下已分配的用户数量，下限
                Integer count = userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
                if (updateReqVO.getAccountCount() < count) {
                    throw exception(LENANT_ALLOCATE_PERSON_COUNT_LESS_THEN_ALLOCATED,
                            count);
                }
            });
        }
        // 根据管理员名称和tenant_id去查询，当前修改的管理员名称是否存在
        if (StringUtils.isNotEmpty(updateReqVO.getContactName())) {
            TenantUtils.execute(tenant.getId(), () -> {
                AdminUserDO user = userService.getUserByUsername(updateReqVO.getContactName());
                if (user != null) {
                    throw exception(USER_USERNAME_EXISTS);
                }
            });
        }
        // 更新租户
        TenantDO updateObj = BeanUtils.toBean(updateReqVO, TenantDO.class);

        if (StringUtils.isNotBlank(updateReqVO.getContactName()) &&
                !updateReqVO.getContactName().equals(tenant.getContactName())) {
            TenantUtils.execute(updateObj.getId(), () -> {

                RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
                Long roleId;
                if (roleDO == null) {
                    // 创建角色
                    roleId = createRole(tenantPackage);
                } else {
                    roleId = roleDO.getId();
                }

                // 移除旧用户角色
                UserRoleDO userRoleDO = permissionService.getUserRoleByUserAndRoleId(tenant.getContactUserId(), roleId);
                if (userRoleDO != null) {
                    permissionService.deleteRoleUsers(roleId, singleton(userRoleDO.getUserId()));
                }
                userService.updateAdminType(tenant.getContactUserId(), AdminTypeEnum.CUSTOM.getType());
                AdminUserDO user = userService.getUserByUsername(updateReqVO.getContactName());
                Long userId;
                if (user != null) {
                    userId = user.getId();
                    // 新管理员分配角色
                    permissionService.assignUserRoles(userId, singleton(roleId));
                } else {
                    // 创建用户，并分配角色
                    TenantInsertReqVO reqVO = new TenantInsertReqVO();
                    reqVO.setContactName(updateReqVO.getContactName());
                    reqVO.setUsername(updateReqVO.getContactName());
                    reqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
                    if (StringUtils.isEmpty(updateReqVO.getPassword())) {
                        reqVO.setPassword(UserPasswordEnum.PASSWORD_ENUM.getPassword());
                    }
                    userId = createUser(roleId, reqVO);
                }

                // 修改租户的管理员
                updateObj.setContactUserId(userId);
            });
            tenantDataRepository.update(updateObj);
            // 如果套餐发生变化，则修改其角色的权限
            if (ObjectUtil.notEqual(tenant.getPackageId(), updateReqVO.getPackageId())) {
                updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
            }
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {

        if (StringUtils.isBlank(name)) {
            return;
        }
        TenantDO tenant = tenantDataRepository.findByName(name);

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
        TenantDO tenant = tenantDataRepository.findByWebsite(website);

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
        tenantDataRepository.deleteById(id);
    }

    private TenantDO validateUpdateTenant(Long id) {
        TenantDO tenant = tenantDataRepository.findById(id);

        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
        if (isPlatformTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantDO getTenant(Long id) {
        return tenantDataRepository.findById(id);
    }


    @Override
    public TenantRespVO getTenantWithAppCount(Long id) {
        TenantDO tenantDO = getTenant(id);
        TenantRespVO tenantRespVO = TenantConvert.INSTANCE.convert(tenantDO);
        CommonResult<Long> appCountResult = appApplicationApi.countApplicationByTenantId(id);
        // Long 转 Integer
        tenantRespVO.setAppCount(appCountResult.getData() != null ? appCountResult.getData().intValue() : 0);
        return tenantRespVO;
    }


    @Override
    public PageResult<TenantDO> getTenantPage(TenantPageReqVO reqVO) {
        return tenantDataRepository.findPage(reqVO);
    }

    @Override
    public TenantDO getTenantByName(String name) {
        return tenantDataRepository.findByName(name);
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
        return tenantDataRepository.findByWebsite(website);
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantDataRepository.countByPackageId(packageId);
    }

    @Override
    public Integer getTenantCountByStatus(Integer status) {
        return (int) tenantDataRepository.countByStatus(status);
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return tenantDataRepository.findAllByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByStatus(Integer status) {
        return tenantDataRepository.findAllByStatus(status);
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
        if (isPlatformTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isPlatformTenant(TenantDO tenant) {
        return Objects.equals(tenant.getTenantCode(), TenantCodeEnum.PLATFORM_TENANT.getCode());
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
