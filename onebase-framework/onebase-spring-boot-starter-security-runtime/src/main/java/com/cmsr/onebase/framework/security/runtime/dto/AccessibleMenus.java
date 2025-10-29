package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 10:45
 */
@Data
public class AccessibleMenus {

    private boolean allAllowed;

    private boolean allDenied;

    private Set<Long> menuIds;
}
