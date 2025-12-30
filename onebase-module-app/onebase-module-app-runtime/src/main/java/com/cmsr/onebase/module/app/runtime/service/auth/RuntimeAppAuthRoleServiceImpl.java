package com.cmsr.onebase.module.app.runtime.service.auth;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleServiceProvider;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleDeptAndUsersReqVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleListRespVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleMembersPageReqVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthRoleMembersPageRespVO;
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
public class RuntimeAppAuthRoleServiceImpl implements RuntimeAppAuthRoleService {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppAuthRoleServiceProvider appAuthRoleServiceProvider;

    @Override
    public List<AuthRoleListRespVO> getRoleList(Long applicationId) {
        List<AppAuthRoleDO> authRoleList = appAuthRoleRepository.findByAppIdAndRoleType(applicationId, AuthRoleTypeEnum.CUSTOM_ROLE.getValue());
        return BeanUtils.toBean(authRoleList, AuthRoleListRespVO.class);
    }

    @Override
    public PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO) {
        return appAuthRoleServiceProvider.pageRoleMembers(reqVO);
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
    public DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO) {
        return appAuthRoleServiceProvider.listDeptUsers(reqVO);
    }


}
