package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.auth.AuthRoleCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.auth.AuthRoleCreateRespVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleListRespVO;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleDeptRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleServiceProvider;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleDeptAndUsersReqVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleMembersPageReqVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleMembersPageRespVO;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
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
public class BuildAppAuthRoleServiceImpl implements BuildAppAuthRoleService {

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

    @Autowired
    private AppAuthRoleServiceProvider appAuthRoleServiceProvider;

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
            AppAuthRoleDO existRole = appAuthRoleRepository.findOneByAppIdAndRoleType(applicationId, AuthRoleTypeEnum.SYSTEM_ADMIN.getValue());
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
            AppAuthRoleDO existRole = appAuthRoleRepository.findOneByAppIdAndRoleType(applicationId, AuthRoleTypeEnum.SYSTEM_USER.getValue());
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
            AppAuthRoleDO existRole = appAuthRoleRepository.findOneByAppIdAndRoleType(applicationId, AuthRoleTypeEnum.OUTER_USER.getValue());
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
        return appAuthRoleServiceProvider.pageRoleMembers(reqVO);
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
        if (AuthRoleTypeEnum.isDefaultRoleTypeRename(authRoleDO.getRoleType())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NOT_ALLOW_RENAME);
        }
        checkRoleNameExists(authRoleDO.getApplicationId(), name, roleId);
        authRoleDO.setRoleName(name);
        appAuthRoleRepository.updateById(authRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(com.cmsr.onebase.module.app.core.vo.auth.AuthRoleAddUserReqVO reqVO) {
        appAuthRoleServiceProvider.addRoleUser(reqVO);
    }

    @Override
    public void addRoleDept(com.cmsr.onebase.module.app.core.vo.auth.AuthRoleAddDeptReqVO reqVO) {
        appAuthRoleServiceProvider.addRoleDept(reqVO);
    }

    @Override
    public void deleteRoleMember(com.cmsr.onebase.module.app.core.vo.auth.AuthRoleDeleteMemberReqVO reqVO) {
        appAuthRoleServiceProvider.deleteRoleMember(reqVO);
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
        return appAuthRoleServiceProvider.listDeptUsers(reqVO);
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
