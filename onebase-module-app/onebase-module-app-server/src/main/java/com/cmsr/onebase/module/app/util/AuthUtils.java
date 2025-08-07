package com.cmsr.onebase.module.app.util;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/8/7 13:33
 */
public class AuthUtils {

    /**
     * 创建角色编码，编码必须英文开头，且只能包含英文、数字、下划线
     *
     * @return
     */
    public static String createRoleCode() {
        return "ROLE_" + UUID.randomUUID().toString().replace("-", "");
    }

}
