package com.cmsr.onebase.module.app.runtime.service.auth;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.vo.auth.*;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface RuntimeAppAuthRoleService {

    List<AuthRoleListRespVO> getRoleList(Long applicationId);

    PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO);

    void addRoleUser(AuthRoleAddUserReqVO reqVO);

    void addRoleDept(AuthRoleAddDeptReqVO reqVO);

    void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO);

    DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO);

}
