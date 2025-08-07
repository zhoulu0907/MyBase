package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleListRespVO;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthRoleService {
    AuthRoleListRespVO getAuthRoleList(Long applicationId);
}
