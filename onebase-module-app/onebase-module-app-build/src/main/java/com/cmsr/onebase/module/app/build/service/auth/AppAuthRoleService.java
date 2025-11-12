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

//    PageResult<AuthRoleUsersPageRespVO> pageRoleUsers(AuthRoleUsersPageReqVO reqVO);

    PageResult<AuthRoleMembersPageRespVO> pageRoleMembers(AuthRoleMembersPageReqVO reqVO);

    AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO);

    void createDefaultRole(Long applicationId);

    void renameRole(Long roleId, String name);

    void addRoleUser(AuthRoleAddUserReqVO reqVO);

//    void deleteRoleUser(AuthRoleDeleteUserReqVO reqVO);

    void addRoleDept(AuthRoleAddDeptReqVO reqVO);

//    void deleteRoleDept(AuthRoleDeleteDeptReqVO reqVO);

    void deleteRoleMember(AuthRoleDeleteMemberReqVO reqVO);

    void deleteRole(Long roleId);

    DeptAndUsersRespDTO listDeptUsers(AuthRoleDeptAndUsersReqVO reqVO);

}
