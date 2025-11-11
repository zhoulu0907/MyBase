package com.cmsr.onebase.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.validation.ValidationUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.infra.api.config.ConfigApi;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.database.AdminUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserPostDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.UserPostDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.dept.PostService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.vo.auth.AuthRegisterReqVO;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.*;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;

/**
 * 后台用户 Service 实现类
 */
@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    static final String USER_INIT_PASSWORD_KEY = "system.user.init-password";

    static final String USER_REGISTER_ENABLED_KEY = "system.user.register-enabled";

    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService tenantService;

    @Resource
    private ConfigApi configApi;
    @Lazy
    @Resource
    private RoleService roleService;

    @Resource
    private AdminUserDataRepository adminUserDataRepository;

    @Resource
    private UserPostDataRepository userPostDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    public Long createUser(UserInsertReqVO createReqVO) {
        // 如果为空，默认为开启状态
        if (createReqVO.getStatus() == null) {
            createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        }
        // 如果是启用状态，校验当前租户下的用户数量有没有超过最大限额
        if (createReqVO.getStatus() == CommonStatusEnum.ENABLE.getStatus()) {
            // 1.1 校验账户配合
            tenantService.handleTenantInfo(tenant -> {
                // 如果用户的租户不是平台租户，则校验租户用户最大限额
                if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                    long count = adminUserDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
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

        // 2.1 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        if (user.getAdminType() == null) {
            user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        //下拉框选择的用户肯定是已经存在的，不需要重新保存
        // adminUserDataRepository.insert(user);
        user.setId(createReqVO.getId());

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

    @Override
    public Long createCorpAdminUser(AdminUserDO userDO) {
        // 校验用户名唯一
        validateUsernameUnique(null, userDO.getUsername());
        // 校验手机号唯一
        validateMobileUnique(null, userDO.getMobile());
        // 校验邮箱唯一
        validateEmailUnique(null, userDO.getEmail());

        userDO.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        if (userDO.getAdminType() == null) {
            userDO.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }

        AdminUserDO adminUserDO= adminUserDataRepository.insert(userDO);
       return adminUserDO.getId();
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
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        if (user.getAdminType() == null) {
            user.setAdminType(AdminTypeEnum.CUSTOM.getType());
        }
        adminUserDataRepository.insert(user);

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
    public Long registerUser(AuthRegisterReqVO registerReqVO) {
        // 1.1 校验是否开启注册
        if (ObjUtil.notEqual(configApi.getConfigValueByKey(USER_REGISTER_ENABLED_KEY).getCheckedData(), "true")) {
            throw exception(USER_REGISTER_DISABLED);
        }
        // 1.2 校验账户配合
        tenantService.handleTenantInfo(tenant -> {
            long count = adminUserDataRepository.count();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.3 校验正确性
        validateUserForCreateOrUpdate(null, registerReqVO.getUsername(), null, null, null, null);

        // 2. 插入用户
        AdminUserDO user = BeanUtils.toBean(registerReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(registerReqVO.getPassword())); //
        adminUserDataRepository.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    public void updateUser(UserUpdateReqVO updateReqVO) {
        // 1. 校验正确性
        AdminUserDO oldUser = validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getMobile(), updateReqVO.getEmail(), updateReqVO.getDeptId(), updateReqVO.getPostIds());
        // 1.1 校验角色权限
        validateRoleIds(updateReqVO.getRoleIds());
        if (updateReqVO.getStatus() != null) {

            if (updateReqVO.getStatus() != oldUser.getStatus() && updateReqVO.getStatus() == CommonStatusEnum.ENABLE.getStatus()) {
                // 1.1 校验账户配合
                tenantService.handleTenantInfo(tenant -> {
                    // 如果用户的租户不是平台租户，则校验租户用户最大限额
                    if (!tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                        long count = adminUserDataRepository.countByConfig(new DefaultConfigStore().eq(AdminUserDO.STATUS,
                                UserStatusEnum.NORMAL.getStatus()));
                        log.info(" count user four tenant, count={}", count);
                        if (count >= tenant.getAccountCount()) {
                            throw exception(USER_COUNT_MAX, tenant.getAccountCount());
                        }
                    }
                });
            }
        }
        // 2.1 更新用户
        AdminUserDO updateObj = BeanUtils.toBean(updateReqVO, AdminUserDO.class);
        adminUserDataRepository.update(updateObj);
        // 2.2 更新岗位
        updateUserPost(updateReqVO, updateObj);
        // 2.3 更新用户角色关联
        permissionService.assignUserRoles(updateReqVO.getId(), updateReqVO.getRoleIds());

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
        adminUserDataRepository.update(new AdminUserDO().setId(id).setAdminType(adminType));
    }

    @Override
    public void updatePlatformUserEmail(Long id, String email) {
        // 校验正确性
        validateUserExists(id);
        // 2.1 更新用户
        adminUserDataRepository.update(new AdminUserDO().setId(id).setEmail(email));
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
        adminUserDataRepository.update(new AdminUserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        // 执行更新
        adminUserDataRepository.update(BeanUtils.toBean(reqVO, AdminUserDO.class).setId(id));
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        AdminUserDO updateObj = new AdminUserDO().setId(id);
        updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // 加密密码
        adminUserDataRepository.update(updateObj);
    }

    @Override
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS)
    public void updateUserPassword(Long id, String password) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);

        // 2. 更新密码
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // 加密密码
        adminUserDataRepository.update(updateObj);

        // 3. 记录操作日志上下文
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
        adminUserDataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    public void deleteUser(Long id) {
        // 1. 校验用户存在
        AdminUserDO user = validateUserExists(id);

        // 2.1 删除用户
        adminUserDataRepository.deleteById(id);
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
        return adminUserDataRepository.findByUsername(username);
    }

    @Override
    public AdminUserDO getUserByMobile(String mobile) {
        return adminUserDataRepository.findByMobile(mobile);
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
        return adminUserDataRepository.findPage(reqVO, deptIds, includeRoleUserIds, excludeRoleUserIds);
    }

    @Override
    public PageResult<AdminUserDO> getSimpleEnableUserPage(UserSimplePageReqVO reqVO) {
        return adminUserDataRepository.findSimpleEnablePage(reqVO);
    }

    @Override
    public AdminUserDO getUser(Long id) {
        return adminUserDataRepository.findById(id);
    }

    @Override
    public List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds) {
        return adminUserDataRepository.findAllByDeptIds(deptIds);
    }

    @Override
    public List<AdminUserDO> getUserListNoDept() {
        return adminUserDataRepository.findAllNoDept();
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
        return adminUserDataRepository.findAllByIds(userIds);
    }

    @Override
    @TenantIgnore
    public List<AdminUserDO> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return adminUserDataRepository.findAllByIds(ids);
    }


    @Override
    @TenantIgnore // 确认忽略租户的方法注解是否有效
    public List<AdminUserDO> getUserListByIgnoreTenantId(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return adminUserDataRepository.findAllByIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<AdminUserDO> users = adminUserDataRepository.findAllByIds(ids);
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
        return adminUserDataRepository.findAllByNicknameLike(nickname);
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
        AdminUserDO user = adminUserDataRepository.findById(id);
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
        AdminUserDO user = adminUserDataRepository.findByUsername(username);
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
        AdminUserDO user = adminUserDataRepository.findByEmail(email);

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
        AdminUserDO user = adminUserDataRepository.findByMobile(mobile);

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
        AdminUserDO user = adminUserDataRepository.findById(id);
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

        // 2. 遍历，逐个创建 or 更新
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
            AdminUserDO existUser = adminUserDataRepository.findByUsername(importUser.getUsername());
            if (existUser == null) {
                adminUserDataRepository.insert(BeanUtils.toBean(importUser, AdminUserDO.class)
                        .setPassword(encodePassword(initPassword)).setPostIds(new HashSet<>()));
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
            adminUserDataRepository.update(updateUser);
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<AdminUserDO> getUserListByStatus(Integer status, String userNickName) {
        return adminUserDataRepository.findAllByStatus(status,userNickName);
    }

    @Override
    public List<AdminUserDO> getPlatformAdminListByStatus(Integer status) {
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
        List<AdminUserDO> users = adminUserDataRepository.findEnableUserByIds(platformAdminUserIds);
        return users;
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public Integer getUserCountByStatus(Integer status) {
        return (int) adminUserDataRepository.countByStatus(status);
    }

    @Override
    public Map<Long, Integer> getUserCountByDeptIds(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyMap();
        }

        // 批量查询指定部门的所有用户（不过滤状态）
        List<AdminUserDO> users = adminUserDataRepository.findAllByDeptIds(deptIds);

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
        List<AdminUserDO> allUsers = adminUserDataRepository.findAllByDeptIds(allDeptIds);

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
    public List<String> getUserRoleByRoleIdAndTenantId(Long id,Long tenantId) {
        List<UserRoleDO> UserRoleDOList=  userRoleDataRepository.getUserRoleByRoleIdAndTenantId(id,tenantId);
        List<String> userIdsList = UserRoleDOList.stream()
                .map(userRole -> String.valueOf(userRole.getUserId()))
                .collect(Collectors.toList());
        return userIdsList;


    }
    @TenantIgnore
    @Override
    public    Map<Long,Integer> getTenantExistUserCountByIds(List<Long> tenantIds) {
        List<AdminUserDO>  userlist= adminUserDataRepository.getTenantExistUserCountByIds(tenantIds);
        // 按租户ID分组并统计数量
        return userlist.stream()
                .collect(Collectors.groupingBy(
                        AdminUserDO::getId,
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

}
