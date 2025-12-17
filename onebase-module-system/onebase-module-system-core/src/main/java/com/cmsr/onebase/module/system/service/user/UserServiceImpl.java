package com.cmsr.onebase.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.validation.ValidationUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.infra.api.config.ConfigApi;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserPostDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.UserDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.UserPostDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.system.enums.dept.DeptCodeEnum;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.enums.user.CreateSourceEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.framework.security.core.PwdEnHelper;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.post.PostService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.vo.auth.AuthRegisterReqVO;
import com.cmsr.onebase.module.system.vo.auth.ThirdAuthLoginReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSaveReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSimpleListRespVO;
import com.cmsr.onebase.module.system.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.vo.user.*;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;
import static java.util.Collections.singleton;

/**
 * 后台用户 Service 实现类
 */
@Slf4j
@Service("userService")
@Validated
public class UserServiceImpl implements UserService {

    static final String USER_INIT_PASSWORD_KEY    = "system.user.init-password";
    static final String USER_REGISTER_ENABLED_KEY = "system.user.register-enabled";

    // 三方用户设置默认密码
    private static final String THIRD_USER_PASSWORD = "ThirdChina2025!";

    @Resource
    private DeptService       deptService;
    @Resource
    private PostService       postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PasswordEncoder   passwordEncoder;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService     tenantService;

    @Resource
    private ConfigApi   configApi;
    @Lazy
    @Resource
    private RoleService roleService;

    @Resource
    private SecurityConfigApi securityConfigApi;

    @Resource
    private UserPostDataRepository userPostDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    @Resource
    private UserDataRepository userDataRepository;

    @Autowired
    private RoleDataRepository roleDataRepository;

    @Resource
    private UserAppRelationService userAppRelationService;


    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private PwdEnHelper pwdEnHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    @CacheEvict(value = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, allEntries = true, beforeInvocation = true)
    public Long createUser(UserInsertReqVO createReqVO) {
        // 如果为空，默认为开启状态
        if (createReqVO.getStatus() == null) {
            createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        }
        // 如果是启用状态，校验当前租户下的用户数量有没有超过最大限额
        if (Objects.equals(CommonStatusEnum.ENABLE.getStatus(), createReqVO.getStatus())) {
            // 1.1 校验账户配合
            tenantService.handleTenantInfo(tenant -> {
                // 如果用户的租户不是平台租户，则校验租户用户最大限额
                if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                    long count = userDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
                            UserStatusEnum.NORMAL.getStatus()));
                    log.info(" count user four tenant, count={}", count);
                    if (count >= tenant.getAccountCount()) {
                        throw exception(USER_COUNT_MAX, tenant.getAccountCount());
                    }
                }
            });
        }
        // 1.2 校验正确性
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(),
                createReqVO.getMobile(), createReqVO.getEmail(), createReqVO.getDeptId(), createReqVO.getPostIds());
        // 1.3 校验角色权限
        validateRoleIds(createReqVO.getRoleIds());
        // 1.4 弱密码校验
        securityConfigApi.validatePassword(createReqVO.getPassword());

        // 2.1 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        // 管理员类型：内置/自定义
        if (user.getAdminType() == null) {
            user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        // 用户类型：根据场景设置,一般和登录用户同范畴, 定义见 UserTypeEnum。以下情况除外：
        // 1. 创建租户时，在平台创建空间管理员，指定 usertype = tenant；
        // 2. 创建企业时，在空间创建企业管理员，指定 usertype = corp；
        if (user.getUserType() == null) {
            user.setUserType(SecurityFrameworkUtils.getLoginUserType());
        }
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }
        if (UserTypeEnum.CORP.getValue().equals(user.getUserType())) {
            user.setCorpId(loginUser.getCorpId());
        }
        userDataRepository.insert(user);
        // 2.1.1 保存初始密码历史记录
        securityConfigApi.savePasswordHistory(user.getId(), user.getPassword());

        // 2.2 插入关联岗位
        if (CollUtil.isNotEmpty(user.getPostIds())) {
            userPostDataRepository.insertBatch(convertList(user.getPostIds(),
                    postId -> new UserPostDO().setUserId(user.getId()).setPostId(postId)));
        }

        // 2.3 插入用户角色关联
        if (CollUtil.isNotEmpty(createReqVO.getRoleIds())) {
            permissionService.assignUserRoles(user.getId(), createReqVO.getRoleIds());
        }

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        return user.getId();
    }

    public void validateCorpAdminUser(AdminUserDO userDO) {
        // 校验用户名唯一
        validateUsernameUnique(null, userDO.getUsername());
        // 校验手机号唯一
        validateMobileUnique(null, userDO.getMobile());
        // 校验邮箱唯一
        validateEmailUnique(null, userDO.getEmail());
    }

    @Override
    public void checkCorpAdminUser(AdminUserDO adminUserDO) {
        validateCorpAdminUser(adminUserDO);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, allEntries = true, beforeInvocation = true)
    public Long createCorpAdminUser(AdminUserDO userDO) {

        // 验证企业管理员信息
        validateCorpAdminUser(userDO);

        userDO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        if (userDO.getAdminType() == null) {
            userDO.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        userDO.setUserType(UserTypeEnum.CORP.getValue());
        AdminUserDO adminUserDO = userDataRepository.insert(userDO);

        // 保存初始密码历史记录
        securityConfigApi.savePasswordHistory(adminUserDO.getId(), adminUserDO.getPassword());

        Long roleId = createCoreAdminRole();
        permissionService.assignUserRoles(adminUserDO.getId(), singleton(roleId));
        return adminUserDO.getId();
    }

    private Long createCoreAdminRole() {
        // 创建角色
        RoleInsertReqVO reqVO = new RoleInsertReqVO();
        reqVO.setName(RoleCodeEnum.CORP_ADMIN.getName()).setCode(RoleCodeEnum.CORP_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.CORP_ADMIN.getCode());
        if (null == roleDO) {
            Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
            return roleId;
        }
        return roleDO.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    @Override
    public Long createPlatformUser(UserInsertReqVO createReqVO) {
        // 如果前端没传管理员昵称，设置平台管理员默认名称
        if (StringUtils.isBlank(createReqVO.getNickname())) {
            createReqVO.setNickname(RoleCodeEnum.SUPER_ADMIN.getName());
        }
        //  校验正确性
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(),
                createReqVO.getMobile(), createReqVO.getEmail(), createReqVO.getDeptId(), createReqVO.getPostIds());
        // 弱密码校验
        securityConfigApi.validatePassword(createReqVO.getPassword());
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        if (user.getAdminType() == null) {
            user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        user.setUserType(UserTypeEnum.PLATFORM.getValue());
        userDataRepository.insert(user);

        // 保存初始密码历史记录
        securityConfigApi.savePasswordHistory(user.getId(), user.getPassword());

        // 赋予平台管理员角色
        RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.SUPER_ADMIN.getCode());
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(roleDO.getId());
        permissionService.assignUserRoles(user.getId(), roleIds);


        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        return user.getId();
    }


    @Override
    @CacheEvict(value = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, allEntries = true, beforeInvocation = true)
    public Long registerUser(AuthRegisterReqVO registerReqVO) {
        // 1.1 校验是否开启注册
        if (ObjUtil.notEqual(configApi.getConfigValueByKey(USER_REGISTER_ENABLED_KEY).getCheckedData(), "true")) {
            throw exception(USER_REGISTER_DISABLED);
        }
        // 1.2 校验账户配合
        tenantService.handleTenantInfo(tenant -> {
            long count = userDataRepository.count();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.3 校验正确性
        validateUserForCreateOrUpdate(null, registerReqVO.getUsername(), null, null, null, null);
        // 1.4 弱密码校验
        securityConfigApi.validatePassword(registerReqVO.getPassword());

        // 2. 插入用户
        AdminUserDO user = BeanUtils.toBean(registerReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(registerReqVO.getPassword())); //
        user.setUserType(registerReqVO.getUserType());
        userDataRepository.insert(user);

        // 2.1 保存初始密码历史记录
        securityConfigApi.savePasswordHistory(user.getId(), user.getPassword());

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    @CacheEvict(value = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, allEntries = true, beforeInvocation = true)
    public void updateUser(UserUpdateReqVO updateReqVO) {
        // 1. 校验正确性
        AdminUserDO oldUser = validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getMobile(), updateReqVO.getEmail(), updateReqVO.getDeptId(), updateReqVO.getPostIds());
        checkTenantUserCountLimit(updateReqVO.getStatus(), oldUser);
        // 2.1 更新用户
        AdminUserDO updateObj = BeanUtils.toBean(updateReqVO, AdminUserDO.class);
        userDataRepository.update(updateObj);
        // 2.2 更新岗位
        updateUserPost(updateReqVO, updateObj);

        // 2.3 更新用户角色关联
        if (CollUtil.isNotEmpty(updateReqVO.getRoleIds())) {
            permissionService.assignUserRoles(updateReqVO.getId(), updateReqVO.getRoleIds());
        }

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldUser, UserInsertReqVO.class));
        LogRecordContext.putVariable("user", oldUser);
    }

    @Override
    @TenantIgnore
    public void updateAdminType(Long id, Integer adminType) {
        // 校验正确性
        validateUserExists(id);
        // 2.1 更新用户管理员类型
        userDataRepository.update(new AdminUserDO().setId(id).setAdminType(adminType));
    }

    @Override
    public void updatePlatformUserEmail(Long id, String email) {
        // 校验正确性
        validateUserExists(id);
        // 2.1 更新用户
        userDataRepository.update(new AdminUserDO().setId(id).setEmail(email));
    }

    private void updateUserPost(UserUpdateReqVO reqVO, AdminUserDO updateObj) {
        Long userId = reqVO.getId();

        Set<Long> dbPostIds = convertSet(userPostDataRepository.findAllByUserId(userId), UserPostDO::getPostId);
        // 计算新增和删除的岗位编号
        Set<Long> postIds = CollUtil.emptyIfNull(updateObj.getPostIds());
        Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIds);
        Collection<Long> deletePostIds = CollUtil.subtract(dbPostIds, postIds);
        // 执行新增和删除。对于已经授权的岗位，不用做任何处理
        if (!CollUtil.isEmpty(createPostIds)) {
            userPostDataRepository.insertBatch(convertList(createPostIds,
                    postId -> new UserPostDO().setUserId(userId).setPostId(postId)));
        }
        if (!CollUtil.isEmpty(deletePostIds)) {
            userPostDataRepository.deleteByUserIdAndPostIds(userId, deletePostIds);
        }
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userDataRepository.update(new AdminUserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        // 执行更新
        userDataRepository.update(BeanUtils.toBean(reqVO, AdminUserDO.class).setId(id));
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 弱密码校验
        securityConfigApi.validatePassword(reqVO.getNewPassword());
        // 历史密码校验
        securityConfigApi.validatePasswordHistory(id, reqVO.getNewPassword());
        // 执行更新
        AdminUserDO updateObj = new AdminUserDO().setId(id);
        updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // 加密密码
        userDataRepository.update(updateObj);
        // 保存密码历史
        securityConfigApi.savePasswordHistory(id, updateObj.getPassword());
    }

    @Override
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS)
    public void updateUserPassword(Long id, String password) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);

        // 2. 弱密码校验
        securityConfigApi.validatePassword(password);

        // 3. 历史密码校验
        securityConfigApi.validatePasswordHistory(id, password);

        // 4. 更新密码
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // 加密密码
        userDataRepository.update(updateObj);

        // 5 保存密码修改历史记录
        securityConfigApi.savePasswordHistory(id, updateObj.getPassword());

        // 6. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户存在
        validateUserExists(id);
        // 更新状态
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        userDataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    @CacheEvict(value = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, allEntries = true, beforeInvocation = true)
    public void deleteUser(Long id) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);
        // 如果是内置系统管理员，不允许删除
        if (AdminTypeEnum.SYSTEM.getType().equals(user.getAdminType())) {
            throw exception(USER_PASSWORD_NOT_ALLOW_DEL);
        }

        // 2.1 删除用户
        userDataRepository.deleteById(id);
        // 2.2 删除用户关联数据
        permissionService.processUserDeleted(id);
        // 2.2 删除用户岗位
        userPostDataRepository.deleteByUserId(id);
        // 2.2 删除用户角色
        appAuthRoleUser.deleteByUserId(id);
        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
    }

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userDataRepository.findByUsername(username);
    }

    @Override
    public AdminUserDO getUserByMobile(String mobile) {
        return userDataRepository.findByMobile(mobile);
    }

    @Override
    public PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO) {
        // 获得部门条件：查询指定部门的子部门编号们，包括自身
        Collection<Long> deptIds = getDeptCondition(reqVO.getDeptId());

        // 如果有角色编号，查询角色对应的用户编号
        Collection<Long> includeRoleUserIds = null, excludeRoleUserIds = null;
        // 过滤拥有roleId该角色的用户
        if (reqVO.getRoleId() != null) {
            includeRoleUserIds = permissionService.getUserIdsListByRoleIds(singleton(reqVO.getRoleId()));
            if (CollUtil.isEmpty(includeRoleUserIds)) {
                // 如果角色下没有用户，直接返回空结果
                return new PageResult<>(Collections.emptyList(), 0L);
            }
        }
        // 排除拥有excludRoleId角色的用户
        if (reqVO.getExcludRoleId() != null) {
            excludeRoleUserIds = permissionService.getUserIdsListByRoleIds(singleton(reqVO.getExcludRoleId()));
        }

        // 分页查询
        return userDataRepository.findPage(reqVO, deptIds, includeRoleUserIds, excludeRoleUserIds);
    }

    @Override
    public PageResult<AdminUserDO> getSimpleEnableUserPage(UserSimplePageReqVO reqVO) {
        return userDataRepository.findSimpleEnablePage(reqVO);
    }

    @Override
    public AdminUserDO getUser(Long id) {
        AdminUserDO adminUserDo = userDataRepository.findById(id);
        return adminUserDo;
    }

    @Override
    public List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds) {
        return userDataRepository.findAllByDeptIds(deptIds);
    }

    @Override
    public List<AdminUserDO> getUserListNoDept() {
        return userDataRepository.findAllNoDept();
    }

    @Override
    public List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds) {
        if (CollUtil.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        List<Long> userIds = convertList(userPostDataRepository.findAllByPostIds(postIds), UserPostDO::getUserId);
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userDataRepository.findAllByIds(userIds);
    }

    @Override
    @TenantIgnore
    public List<AdminUserDO> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userDataRepository.findAllByIds(ids);
    }


    @Override
    @TenantIgnore // 确认忽略租户的方法注解是否有效
    public List<AdminUserDO> getUserListByIgnoreTenantId(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userDataRepository.findAllByIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<AdminUserDO> users = userDataRepository.findAllByIds(ids);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users, AdminUserDO::getId);
        // 校验
        ids.forEach(id -> {
            AdminUserDO user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<AdminUserDO> getUserListByNickname(String nickname) {
        return userDataRepository.findAllByNicknameLike(nickname);
    }

    /**
     * 获得部门条件：查询指定部门的子部门编号们，包括自身
     *
     * @param deptId 部门编号
     * @return 部门编号集合
     */
    private Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) {
            return Collections.emptySet();
        }
        Set<Long> deptIds = convertSet(deptService.getChildDeptList(deptId), DeptDO::getId);
        deptIds.add(deptId); // 包括自身
        return deptIds;
    }

    private AdminUserDO validateUserForCreateOrUpdate(Long id, String username, String mobile, String email,
                                                      Long deptId, Set<Long> postIds) {
        // 校验用户存在
        AdminUserDO user = validateUserExists(id);
        // 校验用户名唯一
        validateUsernameUnique(id, username);
        // 校验手机号唯一
        validateMobileUnique(id, mobile);
        // 校验邮箱唯一
        validateEmailUnique(id, email);
        // 校验部门处于开启状态
        deptService.validateDeptList(CollectionUtils.singleton(deptId));
        // 校验岗位处于开启状态
        postService.validatePostList(postIds);
        return user;
    }

    @VisibleForTesting
    AdminUserDO validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        AdminUserDO user = userDataRepository.findById(id);
        log.info("find user id is {}, user:{}", id, user);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        AdminUserDO user = userDataRepository.findByUsername(username);
        log.info("find user id is {}, user:{}", id, user);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        AdminUserDO user = userDataRepository.findByEmail(email);

        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        AdminUserDO user = userDataRepository.findByMobile(mobile);

        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * 校验旧密码
     *
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        AdminUserDO user = userDataRepository.findById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport) {
        // 1.1 参数校验
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        // 1.2 初始化密码不能为空
        String initPassword = configApi.getConfigValueByKey(USER_INIT_PASSWORD_KEY).getCheckedData();
        if (StrUtil.isEmpty(initPassword)) {
            throw exception(USER_IMPORT_INIT_PASSWORD);
        }
        // 1.3 弱密码校验
        securityConfigApi.validatePassword(initPassword);

        // 2. 遍历,逐个创建 or 更新
        UserImportRespVO respVO = UserImportRespVO.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        importUsers.forEach(importUser -> {
            // 2.1.1 校验字段是否符合要求
            try {
                ValidationUtils.validate(BeanUtils.toBean(importUser, UserInsertReqVO.class).setPassword(initPassword));
            } catch (ConstraintViolationException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }
            // 2.1.2 校验，判断是否有不符合的原因
            try {
                validateUserForCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null);
            } catch (ServiceException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }

            // 2.2.1 判断如果不存在，在进行插入
            AdminUserDO existUser = userDataRepository.findByUsername(importUser.getUsername());
            if (existUser == null) {
                AdminUserDO newUser = BeanUtils.toBean(importUser, AdminUserDO.class)
                        .setPassword(encodePassword(initPassword)).setPostIds(new HashSet<>());
                userDataRepository.insert(newUser);
                // 保存初始密码历史记录
                securityConfigApi.savePasswordHistory(newUser.getId(), newUser.getPassword());
                respVO.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 2.2.2 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                respVO.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            AdminUserDO updateUser = BeanUtils.toBean(importUser, AdminUserDO.class);
            updateUser.setId(existUser.getId());
            userDataRepository.update(updateUser);
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<AdminUserDO> getUserListByStatus(Integer status, String userNickName) {
        return userDataRepository.findAllByStatus(status, userNickName);
    }


    @Override
    public List<AdminUserDO> getUserListByStatusAndDeptId(DeptSimpleListRespVO reqVO) {
        // 获取部门条件：查询指定部门的子部门编号们，包括自身
        Long deptId = reqVO.getDeptId();
        boolean directFlag = reqVO.getDirectFlag() == null || reqVO.getDirectFlag();
        Set<Long> deptIds = new HashSet<>();
        if (directFlag) {
            deptIds.add(deptId);
        } else {
            deptIds = getDeptCondition(deptId);
        }
        return userDataRepository.findAllByStatusAndDeptIds(CommonStatusEnum.ENABLE.getStatus(), deptIds);


    }

    @Override
    public List<UserRespVO> getConvertUserPage(PageResult<AdminUserDO> pageResult) {

        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(pageResult.getList(), AdminUserDO::getDeptId));
        // 部门赋值
        List<UserRespVO> userRespVOList = UserConvert.INSTANCE.convertList(pageResult.getList(), deptMap);

        // 获取用户 ID 列表
        List<Long> userIds = pageResult.getList().stream()
                .map(AdminUserDO::getId)
                .collect(Collectors.toList());

        // 获取用户角色
        List<UserRoleDO> userRoleDOList = userRoleDataRepository.getRoleByUserIds(userIds);

        // 获取角色 ID 列表
        Set<Long> roleIds = userRoleDOList.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toSet());
        // 获取角色列表
        List<RoleDO> roleDOList = roleDataRepository.findAllByIds(roleIds);
        Map<Long, RoleDO> roleDOMap = roleDOList.stream()
                .collect(Collectors.toMap(RoleDO::getId, Function.identity()));

        // 通过用户分组
        Map<Long, List<UserRoleDO>> userRoleMap = userRoleDOList.stream()
                .collect(Collectors.groupingBy(UserRoleDO::getUserId));

        // 封装数据
        userRespVOList.forEach(user -> {
            List<UserRoleDO> userRoles = userRoleMap.getOrDefault(user.getId(), Collections.emptyList());
            List<UserRespVO.UserRoleRespVO> roleRespVOs = userRoles.stream()
                    .map(userRole -> {
                        RoleDO role = roleDOMap.get(userRole.getRoleId());
                        if (null != role) {
                            UserRespVO.UserRoleRespVO roleResp = new UserRespVO.UserRoleRespVO();
                            roleResp.setId(role.getId());
                            roleResp.setName(role.getName());
                            return roleResp;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            user.setRoles(roleRespVOs);
        });
        return userRespVOList;
    }


    @Override
    @TenantIgnore
    public List<AdminUserDO> getPlatformUserByUsernames(Set<String> usernamesList) {
        return userDataRepository.getPlatformUserByUsernames(usernamesList);
    }

    @Override
    public List<AdminUserDO> getPlatformAdminListByStatus(UserSearchReqVO userSearchReqVO) {
        // 获取平台管理员角色
        RoleDO platformAdminRole = roleService.getRoleIdsByCode(RoleCodeEnum.SUPER_ADMIN.getCode());
        if (platformAdminRole == null) {
            return Collections.emptyList();
        }

        // 获取这些用户的角色信息
        List<UserRoleDO> userRoles = new ArrayList<>(userRoleDataRepository.findListByRoleIds(platformAdminRole.getId()));

        // 过滤出具有平台管理员角色的用户
        Set<Long> platformAdminUserIds = convertSet(userRoles, UserRoleDO::getUserId);

        // 获取所有指定状态的用户
        List<AdminUserDO> users = userDataRepository.findEnableUserByIds(platformAdminUserIds, userSearchReqVO.getKeyword(), userSearchReqVO.getStatus());
        return users;
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public Integer getUserCountByStatus(Integer status) {
        return (int) userDataRepository.countByStatus(status);
    }

    @Override
    public Long getUserCountByCorpId(Long corpId) {
        return userDataRepository.getUserCountByCorpId(corpId);
    }

    @Override
    public Map<Long, Integer> getUserCountByDeptIds(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyMap();
        }

        // 批量查询指定部门的所有用户（不过滤状态）
        List<AdminUserDO> users = userDataRepository.findAllByDeptIds(deptIds);

        // 按部门ID分组统计人数
        return users.stream()
                .filter(user -> user.getDeptId() != null)
                .collect(Collectors.groupingBy(
                        AdminUserDO::getDeptId,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
    }

    @Override
    public Map<Long, Integer> getUserCountByDeptIdsIncludeChildren(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyMap();
        }

        // 1. 获取所有部门ID及其子部门ID
        Set<Long> allDeptIds = new HashSet<>();
        for (Long deptId : deptIds) {
            // 添加当前部门ID
            allDeptIds.add(deptId);
            // 添加所有子部门ID
            Set<Long> childDeptIds = deptService.getChildDeptIdListFromCache(deptId);
            allDeptIds.addAll(childDeptIds);
        }

        // 2. 批量查询所有相关部门的用户
        List<AdminUserDO> allUsers = userDataRepository.findAllByDeptIds(allDeptIds);

        // 3. 按部门ID分组统计直属人数
        Map<Long, Integer> directUserCountMap = allUsers.stream()
                .filter(user -> user.getDeptId() != null)
                .collect(Collectors.groupingBy(
                        AdminUserDO::getDeptId,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));

        // 4. 为每个请求的部门计算总人数（包含子部门）
        Map<Long, Integer> result = new HashMap<>();
        for (Long deptId : deptIds) {
            int totalCount = 0;

            // 统计当前部门的直属人数
            totalCount += directUserCountMap.getOrDefault(deptId, 0);

            // 统计所有子部门的人数
            Set<Long> childDeptIds = deptService.getChildDeptIdListFromCache(deptId);
            for (Long childDeptId : childDeptIds) {
                totalCount += directUserCountMap.getOrDefault(childDeptId, 0);
            }

            result.put(deptId, totalCount);
        }

        return result;
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public UserRespVO getUserWithRoles(Long id) {
        // 获取用户基本信息
        AdminUserDO user = getUser(id);
        if (user == null) {
            return null;
        }

        // 获取部门信息
        DeptDO dept = deptService.getDept(user.getDeptId());
        // 获取用户角色信息
        Set<Long> roleIds = permissionService.getRoleIdsListByUserId(id);
        List<RoleDO> roles = new ArrayList<>();
        if (CollUtil.isNotEmpty(roleIds)) {
            roles = roleService.getRoleList(roleIds);
        }

        // 转换为响应对象
        return UserConvert.INSTANCE.convert(user, dept, roles);
    }

    @Override
    public List<String> getUserRoleByRoleIdAndTenantId(Long id, Long tenantId) {
        List<UserRoleDO> UserRoleDOList = userRoleDataRepository.getUserRoleByRoleIdAndTenantId(id, tenantId);
        List<String> userIdsList = UserRoleDOList.stream()
                .map(userRole -> String.valueOf(userRole.getUserId()))
                .collect(Collectors.toList());
        return userIdsList;


    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.USER_FIND_BY_DEPT_IDS, key = "#reqVO.deptId + ':' + #reqVO.pageNo + ':' + #reqVO.pageSize + ':' + (#reqVO.keywords == null ? '' : #reqVO.keywords)")
    public PageResult<AdminUserDO> getUserByDeptPage(UserByDeptPageReqVO reqVO) {
        // 获取需要查询的部门ID列表
        Set<Long> deptIds = new HashSet<>();
        if (Boolean.TRUE.equals(reqVO.getIsRecurseSub())) {
            // 如果需要递归查询子部门，获取当前部门及所有子部门ID
            deptIds.add(reqVO.getDeptId());
            Set<Long> childDeptIds = deptService.getChildDeptIdListFromCache(reqVO.getDeptId());
            deptIds.addAll(childDeptIds);
        } else {
            // 否则只查询当前部门
            deptIds.add(reqVO.getDeptId());
        }

        // 查询指定部门的用户
        return userDataRepository.findEnableUserPageByDeptIds(reqVO, deptIds);
    }

    @Override
    public boolean findAdminByRoleIdAndUserId(Long roleId, Long userId) {
        List<UserRoleDO> userRoleDOList = userRoleDataRepository.findAdminByRoleIdAndUserId(roleId, userId);
        return !userRoleDOList.isEmpty();
    }

    @TenantIgnore
    @Override
    public Map<Long, Integer> getTenantExistUserCountByIds(List<Long> tenantIds) {
        List<AdminUserDO> userlist = userDataRepository.getTenantExistUserCountByIds(tenantIds);
        // 按租户ID分组并统计数量
        return userlist.stream()
                .collect(Collectors.groupingBy(
                        AdminUserDO::getTenantId,
                        Collectors.summingInt(user -> 1)
                ));
    }


    @Override
    public Map<Long, Integer> getCorpExistUserCountByCorpIds(List<Long> corpIds) {
        List<AdminUserDO> userlist = userDataRepository.getCorpExistUserCountByCorpIds(corpIds);
        // 按租户ID分组并统计数量
        return userlist.stream()
                .collect(Collectors.groupingBy(
                        AdminUserDO::getCorpId,
                        Collectors.summingInt(user -> 1)
                ));
    }


    /**
     * 校验角色ID列表的有效性
     *
     * @param roleIds 角色ID集合
     */
    private void validateRoleIds(Set<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return;
        }

        // 校验角色是否存在且有效
        roleService.validateRoleList(roleIds);
    }


    @Override
    public void updateThirdUserPassword(Long id, String password) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);

        // 2. 弱密码校验
        //  securityConfigApi.validatePassword(password);
        // 3. 更新密码 ，默认密码OBThird2025!
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(THIRD_USER_PASSWORD)); // 加密密码
        userDataRepository.update(updateObj);

        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public void forgetPassword(UserForgetPasswordReqVO reqVO) {
        // 1.通过手机号，获取 用户
        AdminUserDO user = userDataRepository.findByMobile(reqVO.getMobile());

        // 2. 弱密码校验
        //  securityConfigApi.validatePassword(reqVO.getPassword());

        // TODO 临时使用默认密码 3. 更新密码,临时使用默认密码
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(user.getId());
        updateObj.setPassword(encodePassword(THIRD_USER_PASSWORD)); // 加密密码
        userDataRepository.update(updateObj);

        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public AdminUserDO createThirdUser(ThirdAuthLoginReqVO reqVO) {

        // 如果是启用状态，校验当前租户下的用户数量有没有超过最大限额
        if (Objects.equals(CommonStatusEnum.ENABLE.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            // 1.1 校验账户配合
            tenantService.handleTenantInfo(tenant -> {
                // 如果用户的租户不是平台租户，则校验租户用户最大限额
                if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                    long count = userDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
                            UserStatusEnum.NORMAL.getStatus()));
                    log.info(" count user four tenant, count={}", count);
                    if (count >= tenant.getAccountCount()) {
                        throw exception(USER_COUNT_MAX, tenant.getAccountCount());
                    }
                }
            });
        }

        AdminUserDO user = new AdminUserDO();
        user.setPassword(encodePassword(THIRD_USER_PASSWORD)); // 加密密码
        // 管理员类型：内置/自定义
        user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        user.setMobile(reqVO.getMobile());
        user.setUserType(UserTypeEnum.THIRD.getValue());
        user.setStatus(UserStatusEnum.NORMAL.getStatus());
        user.setUsername(reqVO.getMobile());
        user.setNickname(" ");
        userDataRepository.insert(user);
        return user;
    }


    @Override
    public ThirdSupplementUserResVO supplementUser(ThirdSupplementUserReqVO reqVO) {
        // 2.1 解密原文
        reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));

        AdminUserDO user = userDataRepository.findById(reqVO.getUserId());
        user.setNickname(reqVO.getNickName());
        user.setEmail(reqVO.getEmail());
        user.setAvatar(reqVO.getAvatar());
        user.setPassword(encodePassword(reqVO.getPassword()));
        userDataRepository.update(user);

        ThirdSupplementUserResVO resVO = new ThirdSupplementUserResVO();
        resVO.setId(user.getId());
        resVO.setUserName(user.getUsername());
        resVO.setNickName(user.getNickname());
        resVO.setEmail(user.getEmail());
        return resVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE_THRID, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    public Long createUserAndUserAppRelation(ThirdUserAppCombinedInsertReqVO reqVO) {
        // 验证用户数据，确保用户不存在
        // 如果为空，默认为开启状态
        if (null == reqVO.getStatus()) {
            reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        }
        // 如果是启用状态，校验当前租户下的用户数量有没有超过最大限额
        if (Objects.equals(CommonStatusEnum.ENABLE.getStatus(), reqVO.getStatus())) {
            // 1.1 校验账户配合
            tenantService.handleTenantInfo(tenant -> {
                // 如果用户的租户不是平台租户，则校验租户用户最大限额
                if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                    long count = userDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
                            UserStatusEnum.NORMAL.getStatus()));
                    log.info(" count user four tenant, count={}", count);
                    if (count >= tenant.getAccountCount()) {
                        throw exception(USER_COUNT_MAX, tenant.getAccountCount());
                    }
                }
            });
        }
        // 校验手机，邮箱
        validateThirdUserForCreateOrUpdate(null, reqVO.getMobile(), reqVO.getEmail());

        if (null != reqVO.getDeptId()) {
            // 三方用户 默认部门，不存在就新建部门
            Long deptId = createThirdDefaultDept();
            reqVO.setDeptId(deptId);
        }


        // 创建用户
        AdminUserDO user = BeanUtils.toBean(reqVO, AdminUserDO.class);
        user.setPassword(encodePassword(THIRD_USER_PASSWORD));
        user.setUsername(reqVO.getMobile());
        user.setUserType(UserTypeEnum.THIRD.getValue());
        user.setStatus(UserStatusEnum.NORMAL.getStatus());
        user.setCreateSource(CreateSourceEnum.BACK.getCode());
        user.setDeptId(reqVO.getDeptId());
        user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        userDataRepository.insert(user);

        // 创建关联关系
        setUserAppRelation(user.getId(), reqVO.getApplicationIdList());

        //  保存初始密码历史记录
        securityConfigApi.savePasswordHistory(user.getId(), user.getPassword());

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);

        return user.getId();
    }


    // 获取三方用户部门是否存在
    private Long createThirdDefaultDept() {
        DeptSaveReqVO deptRespVO = new DeptSaveReqVO();
        deptRespVO.setName(DeptCodeEnum.DEFAULT_THIRD_DEPT.getName());
        deptRespVO.setDeptType(DeptTypeEnum.THIRD.getCode());
        deptRespVO.setDeptCode(DeptCodeEnum.DEFAULT_THIRD_DEPT.getCode());
        DeptDO deptDO = deptService.findDeptByCodeAndType(deptRespVO);

        if (null == deptDO) {
            deptRespVO.setParentId(DeptDO.PARENT_ID_ROOT);
            deptRespVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
            return deptService.createThirdDefaultDept(deptRespVO);
        }
        return deptDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE_THRID, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    public Long updateUserAndUserAppRelation(ThirdUserAppCombinedUpdateReqVO updateReqVO) {
        AdminUserDO oldUser = validateThirdUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getMobile(), updateReqVO.getEmail());
        checkTenantUserCountLimit(updateReqVO.getStatus(), oldUser);
        // 2.1 更新用户
        AdminUserDO updateObj = BeanUtils.toBean(updateReqVO, AdminUserDO.class);
        userDataRepository.update(updateObj);
        // 创建关联关系
        setUserAppRelation(updateReqVO.getId(), updateReqVO.getApplicationIdList());
        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldUser, UserInsertReqVO.class));
        LogRecordContext.putVariable("user", oldUser);
        return updateReqVO.getId();
    }

    private void checkTenantUserCountLimit(Integer status, AdminUserDO oldUser) {
        if (status != null) {
            if (!status.equals(oldUser.getStatus()) && status == CommonStatusEnum.ENABLE.getStatus()) {
                // 1.1 校验账户配合
                tenantService.handleTenantInfo(tenant -> {
                    // 如果用户的租户不是平台租户，则校验租户用户最大限额
                    if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                        long count = userDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
                                UserStatusEnum.NORMAL.getStatus()));
                        log.info(" count user four tenant, count={}", count);
                        if (count >= tenant.getAccountCount()) {
                            throw exception(USER_COUNT_MAX, tenant.getAccountCount());
                        }
                    }
                });
            }
        }
    }

    private void setUserAppRelation(Long userId, List<Long> applicationIdList) {
        UserAppRelationInertReqVO relationInertReqVO = new UserAppRelationInertReqVO();
        relationInertReqVO.setUserId(userId);
        relationInertReqVO.setApplicationIdList(applicationIdList);
        userAppRelationService.createUserAppRelation(relationInertReqVO);
    }

    private AdminUserDO validateThirdUserForCreateOrUpdate(Long id, String mobile, String email) {
        // 校验用户存在
        AdminUserDO user = validateUserExists(id);
        // 校验手机号唯一
        validateMobileUnique(null, mobile);
        // 校验邮箱唯一
        validateEmailUnique(id, email);
        return user;
    }


    @Override
    public PageResult<UserApplicationRespVO> getUserAppRelationPage(UserAppPageSearchReqVO userReqVO) {
        // 1. 查询用户分页数据
        PageResult<AdminUserDO> pageResult = userDataRepository.getThirdUserPage(userReqVO);
        List<AdminUserDO> userDOList = pageResult.getList();

        // 2. 如果没有用户数据，直接返回空结果
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(userDOList)) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }

        // 3. 提取用户ID集合，用于后续查询用户应用关联关系
        Set<Long> userIds = userDOList.stream().map(AdminUserDO::getId).collect(Collectors.toSet());
        UserAppPageReqVO vo = new UserAppPageReqVO();
        vo.setUserIds(userIds);

        // 4. 查询用户与应用的关联关系列表
        List<UserAppRelationDO> userAppRelationList = userAppRelationService.getUserAppRelationList(vo);

        // 5. 如果没有关联关系数据，只返回用户基本信息
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(userAppRelationList)) {
            return new PageResult<>(pageResult.getList().stream()
                    .map(user -> {
                        UserApplicationRespVO userApplicationRespVO = BeanUtils.toBean(user, UserApplicationRespVO.class);
                        userApplicationRespVO.setId(user.getId());
                        return userApplicationRespVO;
                    })
                    .collect(Collectors.toList()), pageResult.getTotal());
        }

        // 6. 按用户ID分组关联关系数据，方便后续匹配
        Map<Long, List<UserAppRelationDO>> userAppRelationMap = userAppRelationList.stream()
                .collect(Collectors.groupingBy(UserAppRelationDO::getUserId));

        // 7. 提取所有关联的应用ID，并查询应用详细信息
        Set<Long> appId = userAppRelationList.stream().map(UserAppRelationDO::getApplicationId).collect(Collectors.toSet());
        List<ApplicationDTO> appList = appApplicationApi.findAppApplicationByAppIds(appId);
        Map<Long, ApplicationDTO> appMap = appList.stream().collect(Collectors.toMap(ApplicationDTO::getId, item -> item));

        // 8. 构建最终的返回结果，包含用户信息和关联的应用信息
        return new PageResult<>(pageResult.getList().stream()
                .map(user -> {
                    UserApplicationRespVO userApplicationRespVO = BeanUtils.toBean(user, UserApplicationRespVO.class);
                    userApplicationRespVO.setId(user.getId());

                    // 9. 获取当前用户关联的应用关系列表
                    List<UserAppRelationDO> appRelationDOList = userAppRelationMap.get(user.getId());
                    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(appRelationDOList)) {
                        // 10. 将应用关系转换为应用详情信息
                        userApplicationRespVO.setUserApplicationList(appRelationDOList.stream()
                                .map(appRelationDO -> {
                                    UserAppVO userAppVO = new UserAppVO();
                                    ApplicationDTO appDTO = appMap.get(appRelationDO.getApplicationId());
                                    if (null != appDTO) {
                                        userAppVO.setAppId(appDTO.getId());
                                        userAppVO.setAppName(appDTO.getAppName());
                                        userAppVO.setIconName(appDTO.getIconName());
                                        userAppVO.setIconColor(appDTO.getIconColor());
                                    }
                                    return userAppVO;
                                })
                                .collect(Collectors.toList())
                        );
                    }
                    return userApplicationRespVO;
                })
                .collect(Collectors.toList()), pageResult.getTotal());
    }

    @Override
    public Long thirdUserRegister(ThirdUserRegisterReqVO reqVO) {
       // createThirdUser(reqVO);
        return  null;
    }
}
