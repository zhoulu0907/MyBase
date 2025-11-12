package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.app.core.provider.AppCacheProvider;
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
    private AppAuthRoleRepository appAuthRoleRepository;

    @Resource
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Resource
    private AppAuthRoleDeptRepository appAuthRoleDeptRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private DeptApi deptApi;

    @Resource
    private AppCacheProvider appCacheProvider;

    @Resource
    private AppSqlQueryRepository appSqlQueryRepository;

    @Override
    public List<AuthRoleListRespVO> getRoleList(Long applicationId) {
        appCommonService.validateApplicationExist(applicationId);
        List<AuthRoleDO> authRoleList = appAuthRoleRepository.findByApplicationId(applicationId);
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }

//    @Override
//    public PageResult<AuthRoleUsersPageRespVO> pageRoleUsers(AuthRoleUsersPageReqVO reqVO) {
//        appCommonService.validateRoleExist(reqVO.getRoleId());
//        PageResult<AuthRoleUserDO> pageResult = appAuthRoleUserRepository.findByRoleId(reqVO.getRoleId(), reqVO);
//        Set<Long> userIds = pageResult.getList().stream().map(v -> v.getUserId()).collect(Collectors.toSet());
//        AppCommonService.UserHelper userHelper = appCommonService.getUserHelper(userIds);
//        List<AuthRoleUsersPageRespVO> respVOS = userIds.stream().map(userId -> {
//            AdminUserRespDTO user = userHelper.getUser(userId);
//            if (user == null) {
//                AuthRoleUsersPageRespVO vo = new AuthRoleUsersPageRespVO();
//                vo.setId(userId);
//                vo.setNickname("[" + userId + "]");
//                vo.setMobile("-");
//                return vo;
//            } else {
//                return BeanUtils.toBean(user, AuthRoleUsersPageRespVO.class);
//            }
//        }).toList();
//        return new PageResult(respVOS, pageResult.getTotal());
//    }

    @Override
    public PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        PageResult<RoleMemberDTO> result = appSqlQueryRepository.findRoleMembers(reqVO.getRoleId(), reqVO.getMemberName(), reqVO.getMemberType(), reqVO);
        List<AuthRoleMembersPageRespVO> voResult = result.getList().stream().map(v -> {
            AuthRoleMembersPageRespVO vo = new AuthRoleMembersPageRespVO();
            vo.setId(v.getId());
            vo.setMemberId(v.getMemberId());
            vo.setName(v.getMemberName());
            vo.setType(v.getMemberType());
            vo.setDeptName(v.getDeptName());
            if (RoleMemberDTO.MEMBER_TYPE_USER.equals(v.getMemberType())) {
                vo.setTypeName("成员");
            } else if (v.getIsIncludeChild() != null && v.getIsIncludeChild() == 0) {
                vo.setTypeName("本部门");
            } else if (v.getIsIncludeChild() != null && v.getIsIncludeChild() == 1) {
                vo.setTypeName("本部门及子部门");
            }
            return vo;
        }).toList();
        return new PageResult(voResult, result.getTotal());
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
        appAuthRoleRepository.insert(authRoleDO);
        return BeanUtils.toBean(authRoleDO, AuthRoleCreateRespVO.class);
    }

    @Override
    public void createDefaultRole(Long applicationId) {
        appCommonService.validateApplicationExist(applicationId);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        {
            AuthRoleDO authRoleDO = new AuthRoleDO();
            authRoleDO.setApplicationId(applicationId);
            authRoleDO.setRoleCode(AuthRoleTypeEnum.SYSTEM_ADMIN.getCode());
            authRoleDO.setRoleName(AuthRoleTypeEnum.SYSTEM_ADMIN.getName());
            authRoleDO.setRoleType(AuthRoleTypeEnum.SYSTEM_ADMIN.getValue());
            appAuthRoleRepository.insert(authRoleDO);
            //创建者是管理员
            AuthRoleUserDO entity = new AuthRoleUserDO();
            entity.setUserId(userId);
            entity.setRoleId(authRoleDO.getId());
        }
        {
            AuthRoleDO authRoleDO = new AuthRoleDO();
            authRoleDO.setApplicationId(applicationId);
            authRoleDO.setRoleCode(AuthRoleTypeEnum.SYSTEM_USER.getCode());
            authRoleDO.setRoleName(AuthRoleTypeEnum.SYSTEM_USER.getName());
            authRoleDO.setRoleType(AuthRoleTypeEnum.SYSTEM_USER.getValue());
            appAuthRoleRepository.insert(authRoleDO);
            //创建者是用户
            AuthRoleUserDO entity = new AuthRoleUserDO();
            entity.setUserId(userId);
            entity.setRoleId(authRoleDO.getId());
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
        appAuthRoleRepository.update(authRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(AuthRoleAddUserReqVO reqVO) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.addRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
        appCacheProvider.usersChanged(authRoleDO.getApplicationId(), reqVO.getUserIds());
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteRoleUser(AuthRoleDeleteUserReqVO reqVO) {
//        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
//        appAuthRoleUserRepository.deleteRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
//        appCacheProvider.usersChanged(authRoleDO.getApplicationId(), reqVO.getUserIds());
//    }

    @Override
    public void addRoleDept(AuthRoleAddDeptReqVO reqVO) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleDeptRepository.addRoleDept(reqVO.getRoleId(), reqVO.getDeptIds(), reqVO.getIsIncludeChild());
        appCacheProvider.deptsChanged(authRoleDO.getApplicationId(), reqVO.getDeptIds(), reqVO.getIsIncludeChild());
    }

//    @Override
//    public void deleteRoleDept(AuthRoleDeleteDeptReqVO reqVO) {
//        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
//        appAuthRoleDeptRepository.deleteRoleDept(reqVO.getRoleId(), reqVO.getDeptIds());
//        appCacheProvider.deptsChanged(authRoleDO.getApplicationId(), reqVO.getDeptIds(), 1);
//    }

    @Override
    public void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        for (AuthRoleDeleteMemberReqVO.DeleteMember member : reqVO.getMembers()) {
            if (RoleMemberDTO.MEMBER_TYPE_USER.equalsIgnoreCase(member.getMemberType())) {
                AuthRoleUserDO authRoleUserDO = appAuthRoleUserRepository.findById(member.getId());
                if (authRoleUserDO != null) {
                    appAuthRoleUserRepository.deleteById(member.getId());
                    appCacheProvider.usersChanged(authRoleDO.getApplicationId(), List.of(authRoleUserDO.getUserId()));
                }
            }
            if (RoleMemberDTO.MEMBER_TYPE_DEPT.equalsIgnoreCase(member.getMemberType())) {
                AuthRoleDeptDO authRoleDeptDO = appAuthRoleDeptRepository.findById(member.getId());
                if (authRoleDeptDO != null) {
                    appAuthRoleDeptRepository.deleteById(member.getId());
                    appCacheProvider.deptChanged(authRoleDO.getApplicationId(), authRoleDeptDO.getDeptId(), authRoleDeptDO.getIsIncludeChild());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        AuthRoleDO authRoleDO = appCommonService.validateRoleExist(roleId);
        if (AuthRoleTypeEnum.isSystemRoleType(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_DELETE);
        }
        List<Long> userIds = appAuthRoleUserRepository.findByRoleId(roleId).stream().map(v -> v.getUserId()).toList();
        appAuthRoleUserRepository.deleteByRoleId(roleId);
        appAuthRoleDeptRepository.deleteByRoleId(roleId);
        appAuthRoleRepository.deleteById(roleId);
        appCacheProvider.usersChanged(authRoleDO.getApplicationId(), userIds);
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
        AuthRoleDO authRoleDO = appAuthRoleRepository.findByApplicationIdAndRoleName(applicationId, roleName);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }

    private void checkRoleNameExists(Long applicationId, String roleName, Long roleId) {
        AuthRoleDO authRoleDO = appAuthRoleRepository.findByApplicationIdAndRoleNameAndIdNot(applicationId, roleName, roleId);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }


}
