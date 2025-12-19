package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:56
 */
@Slf4j
@Setter
@Service
public class AppAuthRoleServiceImpl implements AppAuthRoleService {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Autowired
    private AppAuthRoleDeptRepository appAuthRoleDeptRepository;

    @Autowired
    private AppCommonService appCommonService;

    @Autowired
    private DeptApi deptApi;

    @Override
    public List<AuthRoleListRespVO> getRoleList(Long applicationId) {
        AppApplicationDO applicationDO = appCommonService.validateApplicationExist(applicationId);
        try {
            createDefaultRole(applicationId, applicationDO.getCreator());
        } catch (Exception e) {
            log.warn("创建默认角色失败", e);
        }
        List<AppAuthRoleDO> authRoleList = appAuthRoleRepository.findByApplicationId(applicationId);
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }

    @Override
    public void createDefaultRole(Long applicationId, Long userId) {
        {
            AppAuthRoleDO existRole = appAuthRoleRepository.findByAppIdAndRoleCode(applicationId, AuthRoleTypeEnum.SYSTEM_ADMIN.getCode());
            if (existRole == null) {
                AppAuthRoleDO authRoleDO = new AppAuthRoleDO();
                authRoleDO.setApplicationId(applicationId);
                authRoleDO.setRoleUuid(UuidUtils.getUuid());
                authRoleDO.setRoleCode(AuthRoleTypeEnum.SYSTEM_ADMIN.getCode());
                authRoleDO.setRoleName(AuthRoleTypeEnum.SYSTEM_ADMIN.getName());
                authRoleDO.setRoleType(AuthRoleTypeEnum.SYSTEM_ADMIN.getValue());
                appAuthRoleRepository.save(authRoleDO);
                //创建者是管理员
                AppAuthRoleUserDO entity = new AppAuthRoleUserDO();
                entity.setUserId(userId);
                entity.setRoleId(authRoleDO.getId());
                appAuthRoleUserRepository.save(entity);
            } else {
                long count = appAuthRoleUserRepository.countByRoleId(existRole.getId());
                if (count == 0) {
                    AppAuthRoleUserDO entity = new AppAuthRoleUserDO();
                    entity.setUserId(userId);
                    entity.setRoleId(existRole.getId());
                    appAuthRoleUserRepository.save(entity);
                }
            }
        }
        {
            AppAuthRoleDO existRole = appAuthRoleRepository.findByAppIdAndRoleCode(applicationId, AuthRoleTypeEnum.SYSTEM_USER.getCode());
            if (existRole == null) {
                AppAuthRoleDO authRoleDO = new AppAuthRoleDO();
                authRoleDO.setApplicationId(applicationId);
                authRoleDO.setRoleUuid(UuidUtils.getUuid());
                authRoleDO.setRoleCode(AuthRoleTypeEnum.SYSTEM_USER.getCode());
                authRoleDO.setRoleName(AuthRoleTypeEnum.SYSTEM_USER.getName());
                authRoleDO.setRoleType(AuthRoleTypeEnum.SYSTEM_USER.getValue());
                appAuthRoleRepository.save(authRoleDO);
            }
        }
        {
            AppAuthRoleDO existRole = appAuthRoleRepository.findByAppIdAndRoleCode(applicationId, AuthRoleTypeEnum.OUTER_USER.getCode());
            if (existRole == null) {
                AppAuthRoleDO authRoleDO = new AppAuthRoleDO();
                authRoleDO.setApplicationId(applicationId);
                authRoleDO.setRoleUuid(UuidUtils.getUuid());
                authRoleDO.setRoleCode(AuthRoleTypeEnum.OUTER_USER.getCode());
                authRoleDO.setRoleName(AuthRoleTypeEnum.OUTER_USER.getName());
                authRoleDO.setRoleType(AuthRoleTypeEnum.OUTER_USER.getValue());
                appAuthRoleRepository.save(authRoleDO);
            }
        }
    }


    @Override
    public PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        PageResult<RoleMemberDTO> result = appAuthRoleRepository.findRoleMembers(reqVO.getRoleId(), reqVO.getMemberName(), reqVO.getMemberType(), reqVO);
        List<AuthRoleMembersPageRespVO> voResult = result.getList().stream().map(v -> {
            AuthRoleMembersPageRespVO vo = new AuthRoleMembersPageRespVO();
            vo.setId(v.getId());
            vo.setMemberId(v.getMemberId());
            vo.setName(v.getMemberName());
            vo.setAvatar(v.getAvatar());
            vo.setAccount(v.getAccount());
            vo.setCreateSourceText(toCreateSourceText(v.getCreateSource()));
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

    private String toCreateSourceText(String createSource) {
        if (createSource == null || createSource.isEmpty()) {
            return "";
        }
        if ("back".equals(createSource)) {
            return "后台注册";
        }
        if ("self".equals(createSource)) {
            return "自主注册";
        }
        return "";
    }

    @Override
    public AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        checkRoleNameExists(reqVO.getApplicationId(), reqVO.getRoleName());
        AppAuthRoleDO authRoleDO = new AppAuthRoleDO();
        authRoleDO.setApplicationId(reqVO.getApplicationId());
        authRoleDO.setRoleUuid(UuidUtils.getUuid());
        authRoleDO.setRoleType(AuthRoleTypeEnum.CUSTOM_ROLE.getValue());
        authRoleDO.setRoleName(reqVO.getRoleName());
        authRoleDO.setRoleCode(AuthUtils.createRoleCode());
        appAuthRoleRepository.save(authRoleDO);
        return BeanUtils.toBean(authRoleDO, AuthRoleCreateRespVO.class);
    }


    @Override
    public void renameRole(Long roleId, String name) {
        AppAuthRoleDO authRoleDO = appCommonService.validateRoleExist(roleId);
        if (AuthRoleTypeEnum.isDefaultRoleType(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_RENAME);
        }
        checkRoleNameExists(authRoleDO.getApplicationId(), name, roleId);
        authRoleDO.setRoleName(name);
        appAuthRoleRepository.updateById(authRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(AuthRoleAddUserReqVO reqVO) {
        AppAuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.addRoleUser(reqVO.getRoleId(), reqVO.getUserIds());
    }

    @Override
    public void addRoleDept(AuthRoleAddDeptReqVO reqVO) {
        AppAuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleDeptRepository.addRoleDept(reqVO.getRoleId(), reqVO.getDeptIds(), reqVO.getIsIncludeChild());
    }

    @Override
    public void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO) {
        AppAuthRoleDO authRoleDO = appCommonService.validateRoleExist(reqVO.getRoleId());
        for (AuthRoleDeleteMemberReqVO.DeleteMember member : reqVO.getMembers()) {
            if (RoleMemberDTO.MEMBER_TYPE_USER.equalsIgnoreCase(member.getMemberType())) {
                AppAuthRoleUserDO authRoleUserDO = appAuthRoleUserRepository.getById(member.getId());
                if (authRoleUserDO != null) {
                    appAuthRoleUserRepository.removeById(member.getId());
                }
            }
            if (RoleMemberDTO.MEMBER_TYPE_DEPT.equalsIgnoreCase(member.getMemberType())) {
                AppAuthRoleDeptDO authRoleDeptDO = appAuthRoleDeptRepository.getById(member.getId());
                if (authRoleDeptDO != null) {
                    appAuthRoleDeptRepository.removeById(member.getId());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        AppAuthRoleDO authRoleDO = appCommonService.validateRoleExist(roleId);
        if (AuthRoleTypeEnum.isDefaultRoleType(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_DELETE);
        }
        appAuthRoleUserRepository.deleteByRoleId(roleId);
        appAuthRoleDeptRepository.deleteByRoleId(roleId);
        appAuthRoleRepository.removeById(roleId);
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
        AppAuthRoleDO authRoleDO = appAuthRoleRepository.findByApplicationIdAndRoleName(applicationId, roleName);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }

    private void checkRoleNameExists(Long applicationId, String roleName, Long roleId) {
        AppAuthRoleDO authRoleDO = appAuthRoleRepository.findByApplicationIdAndRoleNameAndIdNot(applicationId, roleName, roleId);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }


}
