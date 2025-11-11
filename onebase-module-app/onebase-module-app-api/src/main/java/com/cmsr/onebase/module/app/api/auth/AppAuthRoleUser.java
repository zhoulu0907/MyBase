package com.cmsr.onebase.module.app.api.auth;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:29
 */
public interface AppAuthRoleUser {

    void deleteByUserId(Long userId);

    List<Long> findUserIdsByRoleIds(List<Long> roleIds);

    List<Long> findRoleIdsByAppId(Long appId);

}
