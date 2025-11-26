package com.cmsr.onebase.framework.common.security.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:31
 */
@Data
public class RuntimeLoginUser extends LoginUser {
    /**
     * 应用ID
     */
    private Long applicationId;
}
