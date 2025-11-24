package com.cmsr.onebase.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.tenant.config.TenantProperties;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.system.api.user.AdminUserRoleApi;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.database.TenantDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.PackageTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantStatusEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantInfoHandler;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantMenuHandler;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.vo.tenant.*;
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    // 租户管理员设置默认密码
    private static final String TENANT_ADMIN_PASSWORD = "AdminChina2025!";

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Resource // 由于 onebase.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private UserService          tenantUserService;

    @Resource
    private RoleService       roleService;
    @Resource
    private MenuService       menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private LicenseService    licenseService;
    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private TenantDataRepository tenantDataRepository;

    @Resource
    private CorpService corpService;

    @Resource
    private AdminUserRoleApi adminUserRoleApi;

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
        LicenseDO license = licenseService.getLatestActiveLicense();
        Integer userCount = tenantUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
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
            return Long.valueOf(tenantUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus()));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenant(TenantInsertReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        if (StringUtils.isEmpty(createReqVO.getWebsite())) {
            throw exception(TENANT_WEBSITE_IS_NULL);
        } else {
            // 校验租户域名是否重复
            validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        }
        // 根据租户套餐编号获取租户套餐
        TenantPackageDO tenantPackage = tenantPackageService.getTenantPackageByCode(PackageTypeEnum.ALL.getCode());
        createReqVO.setPackageId(tenantPackage.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse("2099-02-19 00:00:00", formatter);
        if (createReqVO.getExpireTime() == null) {
            createReqVO.setExpireTime(expireTime);
        }

        LicenseDO license = licenseService.getLatestActiveLicense();
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

        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
        tenant.setPublishModel(createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue() : createReqVO.getPublishModel());
        tenant = tenantDataRepository.insert(tenant);

        // 创建租户的管理员1
        TenantDO finalTenant = tenant;
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建管理员角色
            Long roleId = createTenantAdminRole();
            // 创建用户，并分配角色
            createSystemUser(roleId, createReqVO);
        });
        return tenant.getId();
    }


    private Long getExistTenantCount() {
        // 排除平台租户
        Long existTenantCount = tenantDataRepository.countByStatusExcludePlatform(
                TenantStatusEnum.NORMAL.getStatus(), null);
        return existTenantCount;
    }

    private void createSystemUser(Long roleId, TenantInsertReqVO insertReqVO) {
        List<TenantAdminUserReqVO> adminUserReqVOList = insertReqVO.getTenantAdminUserReqVOList();
        adminUserReqVOList.forEach(adminUserReqVO -> {
            UserInsertReqVO reqVO = new UserInsertReqVO();
            reqVO.setUsername(adminUserReqVO.getAdminUserName());
            reqVO.setNickname(adminUserReqVO.getAdminNickName());
            reqVO.setMobile(adminUserReqVO.getAdminMobile());
            reqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
            reqVO.setPassword(TENANT_ADMIN_PASSWORD);
            reqVO.setPlatformUserId(adminUserReqVO.getPlatformUserId());
            // 创建用户
            Long userId = tenantUserService.createUser(reqVO);
            // 分配 管理员角色
            permissionService.assignUserRoles(userId, singleton(roleId));
        });
    }

    private Long createTenantAdminRole() {
        // 创建角色
        RoleInsertReqVO reqVO = new RoleInsertReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        return roleId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(@Valid TenantUpdateReqVO updateReqVO) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());

        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        if (StringUtils.isNotBlank(updateReqVO.getWebsite())) {
            validTenantWebsiteDuplicate(updateReqVO.getWebsite(), updateReqVO.getId());
        }

        LicenseDO license = licenseService.getLatestActiveLicense();
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
            if (updateReqVO.getAccountCount() == null) {
                updateReqVO.setAccountCount(tenant.getAccountCount());
            }
            TenantUtils.execute(tenant.getId(), () -> {
                // 查询当前租户下已分配的用户数量，下限
                Integer count = tenantUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
                if (updateReqVO.getAccountCount() < count) {
                    throw exception(LENANT_ALLOCATE_PERSON_COUNT_LESS_THEN_ALLOCATED,
                            count);
                }
            });
        }
        // 先更新租户
        TenantDO updateObj = BeanUtils.toBean(updateReqVO, TenantDO.class);

        DataRow row = new DataRow();
        row.put(TenantDO.ID, updateObj.getId());
        if (updateObj.getAdminUserId() != null) {
            // row.put(TenantDO.ADMIN_USER_ID, updateObj.getAdminUserId());
        }
        if (StringUtils.isNotEmpty(updateObj.getName())) {
            row.put(TenantDO.NAME, updateObj.getName());
        }
        if (StringUtils.isNotEmpty(updateObj.getWebsite())) {
            row.put(TenantDO.WEBSITE, updateObj.getWebsite());
        }
        if (updateObj.getAccountCount() != null) {
            row.put(TenantDO.ACCOUNT_COUNT, updateObj.getAccountCount());
        }
        if (updateObj.getStatus() != null) {
            row.put(TenantDO.STATUS, updateObj.getStatus());
        }
        tenantDataRepository.updateByConfig(row, new DefaultConfigStore().eq(TenantDO.ID, updateObj.getId()));
        // 修改租户管理员
        if (updateReqVO.getTenantAdminUserUpdateReqVOSList() != null && updateReqVO.getTenantAdminUserUpdateReqVOSList().size() > 0) {
            TenantUtils.execute(updateObj.getId(), () -> {

                // 管理员变了，把旧管理员角色移除，并降级为自定义
                Long roleId = getAdminRoleAndDeleteOldUserRole(tenant);
                // 判断管理员是否发生变更
                List<String> adminUserRespDTOS = adminUserRoleApi.getUserRoleByRoleIdAndTenantId(roleId, tenant.getId());
                List<Long> userIds = adminUserRespDTOS.stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                // 使用AdminUserService批量获取用户数据
                List<AdminUserDO> users = tenantUserService.getUserList(userIds);
                Map<String, Long> usernameIdMap = users.stream()
                        .collect(Collectors.toMap(AdminUserDO::getUsername, AdminUserDO::getId));
                // 删除处理
                Map<String, String> adminUsernameMap = updateReqVO.getTenantAdminUserUpdateReqVOSList().stream()
                        .collect(Collectors.toMap(TenantAdminUserUpdateReqVO::getAdminUserName, TenantAdminUserUpdateReqVO::getAdminUserName));

                // 获取需要删除的用户（在现有用户中但不在新管理员列表中的用户）
                Map<String, Long> usersToDelete = usernameIdMap.entrySet().stream()
                        .filter(entry -> !adminUsernameMap.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                for (Long userId : usersToDelete.values()) {
                    // 移除旧用户租户管理员角色
                    UserRoleDO userRoleDO = permissionService.getUserRoleByUserAndRoleId(userId, roleId);
                    if (userRoleDO != null) {
                        permissionService.deleteRoleUsers(roleId, singleton(userRoleDO.getUserId()));
                    }
                    // 将旧管理员设置为普通用户,降级
                    tenantUserService.updateAdminType(userId, AdminTypeEnum.CUSTOM.getType());
                }

                updateReqVO.getTenantAdminUserUpdateReqVOSList().forEach(adminUserReqVO -> {
                    // 已存在的用户
                    AdminUserDO newAdminUser = tenantUserService.getUserByUsername(adminUserReqVO.getAdminUserName());
                    // 判断前端上送的管理员是否已存在，存在则分配角色且不是老用户
                    if (newAdminUser == null) {
                        // 新管理员用户不存在，创建用户，并分配角色
                        UserInsertReqVO userInsertReqVO = new UserInsertReqVO();
                        userInsertReqVO.setUsername(adminUserReqVO.getAdminUserName());
                        userInsertReqVO.setNickname(adminUserReqVO.getAdminNickName());
                        userInsertReqVO.setMobile(adminUserReqVO.getAdminMobile());
                        userInsertReqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
                        userInsertReqVO.setPassword(TENANT_ADMIN_PASSWORD);
                        userInsertReqVO.setPlatformUserId(adminUserReqVO.getPlatformUserId());
                        // 创建用户
                        Long userId = tenantUserService.createUser(userInsertReqVO);
                        // 分配管理员权限
                        permissionService.assignUserRoles(userId, singleton(roleId));
                    } else {
                        // 新管理员用户存在，直接分配角色
                        permissionService.assignUserRoles(newAdminUser.getId(), singleton(roleId));
                        // 将新管理员设置为内置用户类型
                        tenantUserService.updateAdminType(newAdminUser.getId(), AdminTypeEnum.SYSTEM.getType());
                    }
                });
            });
        }
    }

    private Long getAdminRoleAndDeleteOldUserRole(TenantDO tenant) {
        RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
        Long roleId;
        if (roleDO == null) {
            // 创建角色
            roleId = createTenantAdminRole();
        } else {
            roleId = roleDO.getId();
        }
        return roleId;
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
        // 如果 租户id 为空，则报错已存在同网站的租户
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
        // 仅允许获取自己的租户信息(平台管理员除外)
        boolean isPlatformAdmin = permissionService.isPlatformSuperAdmin(SecurityFrameworkUtils.getLoginUserId());
        if (!isPlatformAdmin) {
            Long loginTenantId = TenantContextHolder.getTenantId();
            if (!Objects.equals(loginTenantId, id)) {
                throw exception(TENANT_ONLY_GET_SELF);
            }
        }

        Map<Long, Integer> corpCountMap = findCorpCount();
        TenantDO tenantDO = getTenant(id);
        // 查询当前租户下的已有的正常状态的用户数量
        Integer count = tenantUserService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        TenantRespVO tenantRespVO = TenantConvert.INSTANCE.convert(tenantDO);
        tenantRespVO.setExistUserCount(count);
        Long appCountResult = appApplicationApi.countApplicationByTenantId(id);
        // Long 转 Integer
        tenantRespVO.setAppCount(appCountResult != null ? appCountResult.intValue() : 0);
        Integer corpCount = corpCountMap.get(tenantDO.getId());
        if (corpCount == null) {
            corpCount = CorpConstant.ZERO; // 默认值处理
        }
        tenantRespVO.setCorpCount(corpCount);
        // 获取当前空间的管理员角色id
        //  RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
        RoleDO roleDO = roleService.getRoleIdsByCodeAndTenantId(RoleCodeEnum.TENANT_ADMIN.getCode(), id);
        if (roleDO != null) {
            List<String> adminUserRespDTOS = adminUserRoleApi.getUserRoleByRoleIdAndTenantId(roleDO.getId(), id);
            List<Long> userIds = adminUserRespDTOS.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            // 使用AdminUserService批量获取用户数据
            // 获取角色对应的管理员
            // 获取租户管理员用户信息
            List<TenantAdminUserResVO> adminUserList = new ArrayList<>();
            if (userIds.size() > 0) {
                List<AdminUserDO> adminUsers = tenantUserService.getUserList(userIds);
                adminUserList = adminUsers.stream()
                        .filter(Objects::nonNull)
                        .map(uservo -> new TenantAdminUserResVO()
                                .setAdminUserName(uservo.getUsername())
                                .setAdminMobile(uservo.getMobile())
                                .setAdminUserId(uservo.getId())
                                .setAdminNickName(uservo.getNickname())
                                .setPlatformUserId(uservo.getPlatformUserId())

                        )
                        .collect(Collectors.toList());
            }
            tenantRespVO.setTenantAdminUserList(adminUserList);
        }
        return tenantRespVO;
    }

    /**
     * 获取所有企业的数据，并根据tenantId分组获取条数
     *
     * @return Map<tenantId, count>
     */
    @TenantIgnore
    public Map<Long, Integer> findCorpCount() {
        List<CorpDO> corpList = corpService.findCorpAll();
        return corpList.stream()
                .collect(Collectors.groupingBy(
                        CorpDO::getTenantId,
                        Collectors.summingInt(corp -> 1) // 使用summingInt替代counting
                ));
    }

    /**
     * 获取所有应用
     *
     * @return
     */
    @TenantIgnore
    public Map<Integer, Integer> findAppCount() {
        return appApplicationApi.findAppApplicationAll();
    }

    @TenantIgnore
    @Override
    public PageResult<TenantRespVO> getTenantPage(TenantPageReqVO reqVO) {
        PageResult<TenantDO> tenantDOPageResult = tenantDataRepository.findPage(reqVO);
        if (CollUtil.isEmpty(tenantDOPageResult.getList())) {
            return PageResult.empty();
        }
        List<TenantDO> tenantDOList = tenantDOPageResult.getList();
        List<Long> tenantIds = CollectionUtils.convertList(tenantDOList, TenantDO::getId);
        Map<Long, Integer> existUserCountMap = tenantUserService.getTenantExistUserCountByIds(tenantIds);
        Map<Long, Integer> coupCountMap = findCorpCount();
        Map<Integer, Integer> appCountMap = findAppCount();
        // 转换为VO并设置昵称

        List<TenantRespVO> tenantRespVOList = tenantDOPageResult.getList().stream()
                .map(tenantDO -> {
                    TenantRespVO tenantRespVO = TenantConvert.INSTANCE.convert(tenantDO);
                    tenantRespVO.setLogoUrl(tenantDO.getLogoUrl());
                    Integer existUserCount = existUserCountMap.get(tenantDO.getId());
                    if (existUserCount == null) {
                        existUserCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setExistUserCount(existUserCount);

                    Integer corpCount = coupCountMap.get(tenantDO.getId());
                    if (corpCount == null) {
                        corpCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setCorpCount(corpCount);

                    Integer appCount = appCountMap.get(tenantDO.getId());
                    if (corpCount == null) {
                        appCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setAppCount(appCount);
                    return tenantRespVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(tenantRespVOList, tenantDOPageResult.getTotal());

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
            menuIds = permissionService.getAllValidActiveMenuIds();
        } else {
            TenantPackageDO tenantPackage = tenantPackageService.getTenantPackage(tenant.getPackageId());
            Set<String> tenantAllPermissions = null;
            if (PackageTypeEnum.ALL.getCode().equals(tenantPackage.getCode())) {
                // 若是 PackageTypeEnum.ALL, tenantAllPermissions = tenant、app开头的权限
                menuIds = permissionService.getAllValidActiveMenuIds();
            } else {
                // 不是All，tenantAllPermissions = package下写入的所有权限点
                menuIds = tenantPackage.getMenuIds();
            }
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