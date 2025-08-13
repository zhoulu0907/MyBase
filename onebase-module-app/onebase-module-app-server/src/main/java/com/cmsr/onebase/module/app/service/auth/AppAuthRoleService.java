package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthRoleService {

    List<AuthRoleListRespVO> getRoleList(Long applicationId);

    AuthRoleCreateRespVO createRole(AuthRoleCreateReqVO reqVO);

    void createDefaultRole(Long applicationId);

    void renameRole(Long roleId, String name);

    void addRoleUser(AuthRoleAddUserReqVO reqVO);

    void deleteRoleUser(AuthRoleDeleteUserReqVO reqVO);

    void deleteRole(Long roleId);

}
