package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.app.service.AppCommonService;
import com.cmsr.onebase.module.app.util.AuthUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public List<AuthRoleListRespVO> getRoleList(Long applicationId) {
        appCommonService.validateApplicationExist(applicationId);
        List<AuthRoleDO> authRoleList = authRoleRepository.findByApplicationId(applicationId);
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }


    @Override
    public AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO) {
        appCommonService.validateApplicationExist(reqVO.getApplicationId());
        checkAuthRoleNameExists(reqVO.getApplicationId(), reqVO.getRoleName());
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
        for (AuthRoleTypeEnum roleType : List.of(AuthRoleTypeEnum.SYSTEM_ADMIN, AuthRoleTypeEnum.SYSTEM_USER)) {
            AuthRoleDO authRoleDO = new AuthRoleDO();
            authRoleDO.setApplicationId(applicationId);
            authRoleDO.setRoleCode(roleType.getCode());
            authRoleDO.setRoleName(roleType.getName());
            authRoleDO.setRoleType(roleType.getValue());
            authRoleRepository.insert(authRoleDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUser(AuthRoleAddUserReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.addRoleUser(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleUser(AuthRoleDeleteUserReqVO reqVO) {
        appCommonService.validateRoleExist(reqVO.getRoleId());
        appAuthRoleUserRepository.deleteRoleUser(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        appAuthRoleUserRepository.deleteByRoleId(roleId);
        authRoleRepository.deleteById(roleId);
    }

    private void checkAuthRoleNameExists(Long applicationId, String roleName) {
        AuthRoleDO authRoleDO = authRoleRepository.findByApplicationIdAndRoleName(applicationId, roleName);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }


}
