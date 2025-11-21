package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthRoleService {

    List<AuthRoleListRespVO> getRoleList(Long applicationId);

    PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO);

    AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO);

    void createDefaultRole(Long applicationId, Long userId);

    void renameRole(Long roleId, String name);

    void addRoleUser(AuthRoleAddUserReqVO reqVO);

    void addRoleDept(AuthRoleAddDeptReqVO reqVO);

    void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO);

    void deleteRole(Long roleId);

    DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO);

}
