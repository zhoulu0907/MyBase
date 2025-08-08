package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleAddMemberReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleDeleteMemberReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthRoleService {

    List<AuthRoleListRespVO> getAuthRoleList(Long applicationId);

    void createRole(AuthRoleCreateReqVO reqVO);

    void createDefaultRole(Long applicationId);

    void addUser(AuthRoleAddMemberReqVO reqVO);

    void deleteUser(AuthRoleDeleteMemberReqVO reqVO);

    void deleteRole(Long roleId);

}
