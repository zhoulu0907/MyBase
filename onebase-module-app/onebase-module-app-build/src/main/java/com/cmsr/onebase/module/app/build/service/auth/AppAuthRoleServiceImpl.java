package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleUserDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:56
 */
@Setter
@Service
public class AppAuthRoleServiceImpl implements AppAuthRoleService {

    @Resource
    private AppAuthRoleRepository authRoleRepository;

    @Resource
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private DeptApi deptApi;

    @Override
    public List<AuthRoleListRespVO> getRoleList(Long applicationId) {
        appCommonService.validateApplicationExist(applicationId);
        List<AuthRoleDO> authRoleList = authRoleRepository.findByApplicationId(applicationId);
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }

    @Override
    public PageResult<AuthRoleUsersPageRespVO> pageRoleUsers(AuthRoleUsersPageReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        PageResult<AuthRoleUserDO> pageResult = appAuthRoleUserRepository.findByRoleId(reqVO.getRoleId(), reqVO);
        Set<Long> userIds = pageResult.getList().stream().map(v -> v.getUserId()).collect(Collectors.toSet());
        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(userIds);
        List<AuthRoleUsersPageRespVO> respVOS = userIds.stream().map(userId -> {
            AdminUserRespDTO user = userHelper.getUser(userId);
            if (user == null) {
                AuthRoleUsersPageRespVO vo = new AuthRoleUsersPageRespVO();
                vo.setId(userId);
                vo.setNickname("[" + userId + "]");
                vo.setMobile("-");
                return vo;
            } else {
                return BeanUtils.toBean(user, AuthRoleUsersPageRespVO.class);
            }
        }).toList();
        return new PageResult(respVOS, pageResult.getTotal());
    }

    @Override
    public AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        checkRoleNameExists(reqVO.getApplicationId(), reqVO.getRoleName());
        AuthRoleDO authRoleDO = new AuthRoleDO();
        authRoleDO.setApplicationId(reqVO.getApplicationId());
        authRoleDO.setRoleType(AuthRoleTypeEnum.CUSTOM_ROLE.getValue());
        authRoleDO.setRoleName(reqVO.getRoleName());
        authRoleDO.setRoleCode(AuthUtils.createRoleCode());
        authRoleRepository.insert(authRoleDO);
        return BeanUtils.toBean(authRoleDO, AuthRoleCreateRespVO.class);
    }

    @Override
    public void createDefaultRole(Long applicationId) {
        appCommonService.validateApplicationExist(applicationId);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        for (AuthRoleTypeEnum roleType : List.of(AuthRoleTypeEnum.SYSTEM_ADMIN, AuthRoleTypeEnum.SYSTEM_USER)) {
            AuthRoleDO authRoleDO = new AuthRoleDO();
            authRoleDO.setApplicationId(applicationId);
            authRoleDO.setRoleCode(roleType.getCode());
            authRoleDO.setRoleName(roleType.getName());
            authRoleDO.setRoleType(roleType.getValue());
            authRoleRepository.insert(authRoleDO);
            if (!AuthRoleTypeEnum.isSystemAdminRole(roleType.getValue())) {
                AuthRoleUserDO entity = new AuthRoleUserDO();
                entity.setUserId(userId);
                entity.setRoleId(authRoleDO.getId());
                appAuthRoleUserRepository.insert(entity);
            }
        }
    }

    @Override
    public void renameRole(Long roleId, String name) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(roleId);
        if (AuthRoleTypeEnum.isSystemRoleType(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_RENAME);
        }
        checkRoleNameExists(authRoleDO.getApplicationId(), name, roleId);
        authRoleDO.setRoleName(name);
        authRoleRepository.update(authRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(AuthRoleAddUserReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.addRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleUser(AuthRoleDeleteUserReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.deleteRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(roleId);
        if (AuthRoleTypeEnum.isSystemRoleType(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_DELETE);
        }
        appAuthRoleUserRepository.deleteById(roleId);
        authRoleRepository.deleteById(roleId);

    }

    @Override
    public DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO) {
        List<Long> userIds = appAuthRoleUserRepository.findByRoleId(reqVO.getRoleId()).stream().map(v -> v.getUserId()).toList();
        DeptAndUsersReqDTO deptAndUsersReqDTO = new DeptAndUsersReqDTO();
        deptAndUsersReqDTO.setDeptId(reqVO.getDeptId());
        deptAndUsersReqDTO.setKeywords(reqVO.getKeywords());
        deptAndUsersReqDTO.setExcludeUserIds(userIds);
        return deptApi.getDeptAndUsers(deptAndUsersReqDTO).getData();
    }

    private void checkRoleNameExists(Long applicationId, String roleName) {
        AuthRoleDO authRoleDO = authRoleRepository.findByApplicationIdAndRoleName(applicationId, roleName);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }

    private void checkRoleNameExists(Long applicationId, String roleName, Long roleId) {
        AuthRoleDO authRoleDO = authRoleRepository.findByApplicationIdAndRoleNameAndIdNot(applicationId, roleName, roleId);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }


}
