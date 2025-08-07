package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleAddMemberReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleDeleteMemberReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleListRespVO;
import com.cmsr.onebase.module.app.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.dal.database.auth.AppAuthUserRoleRepository;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.auth.AuthRoleTypeEnum;
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
    private AppAuthUserRoleRepository authUserRoleRepository;

    @Override
    public List<AuthRoleListRespVO> getAuthRoleList(Long applicationId) {
        List<AuthRoleDO> authRoleList = authRoleRepository.findByApplicationId(applicationId);
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }


    @Override
    public void createAuthRole(AuthRoleCreateReqVO reqVO) {
        checkAuthRoleNameExists(reqVO.getApplicationId(), reqVO.getRoleName());
        AuthRoleDO authRoleDO = new AuthRoleDO();
        authRoleDO.setApplicationId(reqVO.getApplicationId());
        authRoleDO.setRoleType(AuthRoleTypeEnum.CUSTOM_ROLE.getValue());
        authRoleDO.setRoleName(reqVO.getRoleName());
        authRoleDO.setRoleCode(AuthUtils.createRoleCode());
        authRoleRepository.insert(authRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(AuthRoleAddMemberReqVO reqVO) {
        authUserRoleRepository.saveUserRole(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMember(AuthRoleDeleteMemberReqVO reqVO) {
        authUserRoleRepository.deleteUserRole(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthRole(Long roleId) {
        authUserRoleRepository.deleteByRoleId(roleId);
        authRoleRepository.deleteById(roleId);
    }

    private void checkAuthRoleNameExists(Long applicationId, String roleName) {
        AuthRoleDO authRoleDO = authRoleRepository.findByApplicationIdAndRoleName(applicationId, roleName);
        if (authRoleDO != null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_AUTH_ROLE_NAME_EXISTS);
        }
    }

}
